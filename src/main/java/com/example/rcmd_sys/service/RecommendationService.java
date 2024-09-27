package com.example.rcmd_sys.service;

import com.example.rcmd_sys.dto.UserAction;
import com.example.rcmd_sys.dto.UserEventVO;
import com.example.rcmd_sys.model.Store;
import com.example.rcmd_sys.repository.StoreRepository;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static org.apache.spark.sql.functions.*;

@Service
public class RecommendationService {

    @Autowired
    private SparkSession sparkSession;

    @Autowired
    private StoreRepository storeRepository; // JPA 리포지토리 주입
    public void calculateRecommendationScore(UserEventVO userEvent) {

        // 상점 데이터 가져오기
        List<Store> stores = storeRepository.findAll();
        Dataset<Row> storeData = sparkSession.createDataFrame(stores, Store.class);

        // 사용자의 행동 데이터를 스파크 데이터셋으로 변환
        UserAction action = UserAction.builder()
                .userId(userEvent.getUserId())
                .storeId(userEvent.getStoreId())
                .actionType(userEvent.getActionType())
                .timestamp(userEvent.getTimestamp())
                .build();

        List<UserAction> actions = Collections.singletonList(action);

        Dataset<Row> actionData = sparkSession.createDataFrame(actions, UserAction.class);
        // 행동에 따른 가중치 설정 (클릭: 1, 북마크: 2, 가입 시 추천 항목 등록: 3)
        actionData = actionData.withColumn("weight",
                when(col("actionType").equalTo("click"), 1)
                        .when(col("actionType").equalTo("bookmark"), 2)
                        .when(col("actionType").equalTo("taste"), 3)
                        .otherwise(0)
        );

        // 행동에 따른 가중치 설정 (클릭: 1, 북마크: 2, 가입 시 추천 항목 등록: 3)
        actionData = actionData.withColumn("weight",
                when(col("actionType").equalTo("click"), 1)
                        .when(col("actionType").equalTo("bookmark"), 2)
                        .when(col("actionType").equalTo("taste"), 3)
                        .otherwise(0)
        );

        // 모든 상점과 사용자의 행동 데이터를 결합
        Dataset<Row> joinedData = storeData.as("s")
                .join(actionData.as("a"),
                        col("s.storeId").equalTo(col("a.storeId")),
                        "left");


        Dataset<Row> selectedData = joinedData.select(
                col("s.storeId").alias("storeId_s"),
                col("a.storeId").alias("storeId_a"),
                col("a.actionType").alias("actionType_a"), // 행동 타입 추가
                col("a.timestamp").alias("timestamp_a"), // 타임스탬프 추가
                col("weight"), // 가중치 추가
                col("features") // features 추가
        );

        // 상점의 특성을 벡터로 변환
        joinedData = joinedData.withColumn("features",
                array(
                        col("parking").cast("int"),  // 주차 가능 여부 (1 또는 0)
                        col("strollerVal").cast("int") // 유모차 접근성
                )
        );

        // 추천 점수 계산
        Dataset<Row> recommendationScores = selectedData.groupBy("storeId_s")
                .agg(
                        sum("weight").alias("baseScore"),  // 기본 추천 점수
                        first("features").alias("features") // 상점 특성 벡터
                )
                .withColumn("recommendationScore",
                        col("baseScore").plus(
                                col("features").getItem(0).multiply(2) // parking 가중치
                                        .plus(col("features").getItem(1).multiply(1.5)) // strollerVal 가중치
                        )
                );
        // 모든 상점에 대한 추천 점

        recommendationScores.orderBy(col("recommendationScore").desc())
                .select("storeId")
                .distinct() // 중복 제거
                .limit(5) // 상위 5개 결과 제한
                .collectAsList() // List로 변환
                .forEach(row -> System.out.println("Store ID: " + row.getLong(0))); // 각 storeId 출력

        recommendationScores.show(); // Spark의 show() 메서드로 데이터셋 내용 출력
    }
}
