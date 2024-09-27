package com.example.rcmd_sys.config;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@EnableKafka
@Configuration
public class KafkaTemplateConfiguration {

    //생성자에 의존성 주입 그래야 다른데서도 /ㅅ쓸수있ㅇ,ㅁ
    @Bean
    public KafkaTemplate<String, Object> customKafkaTemplate(){
        String bootstrapServers = "localhost:9091,localhost:9092,localhost:9093";
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

       // props.put("bootstrap.servers", bootstrapServers);  -> 브로커 주소
        //props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "1"); //데이터를 복구하지 못하고 유실이 발생할 스도 있지만 all에 비해 빠르긴 함.

        //ProducerFactory를 사용하여 KafkaTemplate 객체를 만들 때는 프로듀서 옵션을 직접 넣는다.

        //빈 객체로 사용할 Kafka Template 인스턴스를 초기화 하고 리턶나다.
        ProducerFactory<String, Object> pf = new DefaultKafkaProducerFactory<>(props);
        return new KafkaTemplate<>(pf);

    }

    // KafkaListenerContainerFactory 설정
    /*
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // 수동 ack 모드
        return factory;
    }*/

    // ConsumerFactory 설정

    /*
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9091,localhost:9092,localhost:9093");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "your-group-id");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }*/
}
