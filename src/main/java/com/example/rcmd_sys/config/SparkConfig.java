package com.example.rcmd_sys.config;

import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Bean
    public SparkSession sparkSession() {
        return SparkSession.builder()
                .appName("Spring-Spark-Recommendation")
                .master("local[*]")  // 클러스터에서 실행할 경우 설정 변경
                .getOrCreate();
    }

}