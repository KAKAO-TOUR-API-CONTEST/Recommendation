package com.example.rcmd_sys;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.*;
import scala.Tuple2;
import static org.apache.spark.sql.functions.col;

public class SparkRecommendationSystem {

    public static void main(String[] args) {

        SparkSession spark = SparkSession.builder()
                .appName("Java Spark Recommendation System")
                .master("local")
                .getOrCreate();

         // CSV 데이터 로드
        Dataset<Row> stores = spark.read()
                .format("csv")
                .option("header", "true")
                .option("inferSchema", "true")
                .load("C://ai-jeju//leisure.csv");

        int selectedStoreId = 1000;

        // 선택한 스토어의 특성 추출
        Row selectedStore = stores.filter("storeId = " + selectedStoreId).first();
        double selectedLocationFeature = Math.sqrt(Math.pow(selectedStore.getDouble(selectedStore.fieldIndex("mapX")), 2) + Math.pow(selectedStore.getDouble(selectedStore.fieldIndex("mapY")), 2));

        // 선택된 store의 벡터 생성
        double[] selectedFeatures = {
                (double) selectedStore.getInt(selectedStore.fieldIndex("categoryId")),
                (double) selectedStore.getInt(selectedStore.fieldIndex("playground")),
                (double) selectedStore.getInt(selectedStore.fieldIndex("parking")),
                (double) selectedStore.getInt(selectedStore.fieldIndex("stroller")),
                (double) selectedStore.getInt(selectedStore.fieldIndex("strollerVal")),
                (double) selectedStore.getInt(selectedStore.fieldIndex("pet")),
                (double) selectedStore.getInt(selectedStore.fieldIndex("babySpareChair")),
                selectedLocationFeature
        };

        Vector selectedVector = Vectors.dense(selectedFeatures);

        // 모든 스토어에 대해 유사도 계산
        Dataset<Tuple2<Integer, Double>> similarityScores = stores.map(
                (MapFunction<Row, Tuple2<Integer, Double>>) row -> {
                    double locationFeature = Math.sqrt(Math.pow(row.getDouble(row.fieldIndex("mapX")), 2) + Math.pow(row.getDouble(row.fieldIndex("mapY")), 2));
                    double[] features = {
                             // Integer를 Double로 변환
                            (double) row.getInt(row.fieldIndex("categoryId")),
                            (double) row.getInt(row.fieldIndex("playground")),
                            (double) row.getInt(row.fieldIndex("parking")),
                            (double) row.getInt(row.fieldIndex("stroller")),
                            (double) row.getInt(row.fieldIndex("strollerVal")),
                            (double) row.getInt(row.fieldIndex("pet")),
                            (double) row.getInt(row.fieldIndex("babySpareChair")),
                            locationFeature
                    };
                     Vector storeVector = Vectors.dense(features);
                    // 유사도 계산 (예: 코사인 유사도)
                    double similarity = computeCosineSimilarity(selectedVector, storeVector);
                    return new Tuple2<>(row.getInt(row.fieldIndex("storeId")), similarity);
                }, Encoders.tuple(Encoders.INT(), Encoders.DOUBLE())
        );
        // 유사도 기준으로 정렬 및 추천 결과 출력 (상위 5개만)
        similarityScores.orderBy(col("_2").desc()).limit(5).show();
    }


    public static double computeCosineSimilarity(Vector v1, Vector v2) {
        double dotProduct = v1.dot(v2);
        double magnitude1 = Math.sqrt(v1.dot(v1));
        double magnitude2 = Math.sqrt(v2.dot(v2));
        return dotProduct / (magnitude1 * magnitude2);
    }

}