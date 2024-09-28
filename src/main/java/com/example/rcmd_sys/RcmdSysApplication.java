package com.example.rcmd_sys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;


@EnableCaching
@EnableKafka
@SpringBootApplication
public class RcmdSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(RcmdSysApplication.class, args);
    }

}
