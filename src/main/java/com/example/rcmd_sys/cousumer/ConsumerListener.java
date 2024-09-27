package com.example.rcmd_sys.cousumer;


import com.example.rcmd_sys.dto.UserAction;
import com.example.rcmd_sys.dto.UserEventVO;
import com.example.rcmd_sys.service.RecommendationService;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Service
public class ConsumerListener {
    private final Logger logger = LoggerFactory.getLogger(ConsumerListener.class);
    private final Gson gson = new Gson();

    @Autowired
    private RecommendationService recommendationService;

    // 하나의 리스너에서 여러가지 토픽을 구독할 수 있다..
    @KafkaListener(topics = {"bookmark-topic", "click-topic", "registration-topic"}, groupId = "my-consumer-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(String message, Acknowledgment acknowledgment) {
        try {
            // JSON 문자열을 UserEventVO 객체로 변환
            UserEventVO userEventVo = gson.fromJson(message, UserEventVO.class);

            // RecommendationService를 이용하여 추천 점수 계산
            recommendationService.calculateRecommendationScore(userEventVo);
            // 수신한 메시지 처리 로직 (예: 유저 행동 데이터 저장 또는 추천 시스템으로 전달)
            logger.info("Received User Event: " + userEventVo);
            // 메시지 처리 완료 후 수동으로 오프셋 커밋
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing message: " + message, e);
        }
    }


}
