package com.example.rcmd_sys.service;

import com.example.rcmd_sys.dto.UserAction;
import com.example.rcmd_sys.dto.UserEventVO;
import com.example.rcmd_sys.model.Store;
import com.example.rcmd_sys.model.StoreSimilarity;
import com.example.rcmd_sys.repository.StoreRepository;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.*;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import scala.Int;
import scala.Tuple2;
import scala.collection.Seq;

import javax.lang.model.type.ArrayType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.apache.spark.sql.functions.*;


@Service
public class RecommendationService {

    @Autowired
    private SparkSession sparkSession;
    @Autowired
    private StoreRepository storeRepository; // JPA 리포지토리 주입

    public void calculateRecommendationScore(UserEventVO userEvent) {
        // 상점 데이터 가져오기
        // 1. 모든 스토어 데이터를 스파크 Dataset으로 변환
        List<Store> stores = storeRepository.findAll();
        Dataset<Row> storeData = sparkSession.createDataFrame(stores, Store.class);

// 2. 사용자의 행동 데이터를 Spark Dataset으로 변환
        UserAction action = UserAction.builder()
                .userId(userEvent.getUserId())
                .storeId(userEvent.getStoreId())
                .actionType(userEvent.getActionType())
                .timestamp(userEvent.getTimestamp())
                .build();
        List<UserAction> actions = Collections.singletonList(action);
        Dataset<Row> actionData = sparkSession.createDataFrame(actions, UserAction.class);

        // 3. 사용자 행동에 따른 가중치 설정 (클릭: 1, 관심: 2, 북마크: 3)
        Column weightedRating = functions
                .when(actionData.col("actionType").equalTo("click"), 1)
                .when(actionData.col("actionType").equalTo("interest"), 2)
                .when(actionData.col("actionType").equalTo("bookmark"), 3)
                .otherwise(0);  // 그 외의 경우

// 4. Store의 속성을 벡터화
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(new String[]{"categoryId", "playground", "stroller", "strollerVal", "pet", "parking", "babySpareChair", "mapX", "mapY"})
                .setOutputCol("features");
        Dataset<Row> storeDataWithFeatures = assembler.transform(storeData);

// 5. 사용자가 선택한 상점의 벡터 추출
        Long selectedStoreId = userEvent.getStoreId();
        Row selectedStoreRow = storeDataWithFeatures
                .filter(storeDataWithFeatures.col("storeId").equalTo(selectedStoreId))
                .first();  // 첫 번째 행 추출
        Vector selectedVector = (Vector) selectedStoreRow.getAs("features");  // features 벡터 추출

// 6. 모든 스토어에 대해 유사도 계산
        Dataset<StoreSimilarity> similarityScores = storeDataWithFeatures.map(
                (MapFunction<Row, StoreSimilarity>) row -> {
                    Vector storeVector = (Vector) row.getAs("features");  // 각 상점의 벡터 추출
                    double similarity = computeCosineSimilarity(selectedVector, storeVector);  // 유사도 계산
                    return new StoreSimilarity(row.getLong(row.fieldIndex("storeId")), similarity);
                }, Encoders.bean(StoreSimilarity.class)  // StoreSimilarity 클래스를 위한 Encoder 사용
        );

        // 7. 유사도 기준으로 정렬 및 상위 5개 추천
        //similarityScores.orderBy(col("similarity").desc()).limit(6).show();
        // 7. 유사도 기준으로 정렬 및 상위 5개 추천
        Dataset<StoreSimilarity> top5Recommendations = similarityScores
                .orderBy(col("similarity").desc())
                .limit(5);

        // StoreSimilarity에서 storeId만 추출하여 리스트로 변환
        List<Long> recommendedStoreIds = top5Recommendations
                .select("storeId") // storeId 컬럼 선택
                .as(Encoders.LONG()) // Long 타입으로 변환
                .collectAsList(); // 리스트로 수집
        // 8. Redis에 상점 추천 데이터를 저장
        saveRecommendationsToRedis(recommendedStoreIds, userEvent.getUserId());


    }

    // 코사인 유사도 계산 함수
    public static double computeCosineSimilarity(Vector v1, Vector v2) {

        double dotProduct = v1.dot(v2);
        double magnitude1 = Math.sqrt(v1.dot(v1));
        double magnitude2 = Math.sqrt(v2.dot(v2));
        // 벡터의 크기가 0인 경우를 처리
        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0.0;
        }
        return dotProduct / (magnitude1 * magnitude2);

    }

    public  void saveRecommendationsToRedis(List<Long> recommendedStoreIds, Long userId) {

       // List<Integer> recommendedStoreIds = new ArrayList<>();
        try (Jedis jedis = new Jedis("localhost", 6379)) {  // Redis 서버에 연결 (기본 포트 6379)
            //jedis.auth("your_password");  // 비밀번호가 있다면 설정 (비밀번호가 없으면 생략 가능)
            String key = "user:" + userId + ":recommendations";  // 사용자별로 저장된 키
            //Set<String> storeIdFields = jedis.hkeys(key);  // 모든 상점 ID 필드 가져오기
            // storeId들을 Long 타입의 리스트로 변환
            for (Long storeId : recommendedStoreIds) {
                jedis.lpush(key, String.valueOf(storeId)); // storeId를 키로 사용하고 값은 비워두기
            }

            System.out.println("추천 상점 storeId들이 반환되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
