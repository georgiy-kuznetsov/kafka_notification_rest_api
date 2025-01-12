package com.gkuznetsov.kafkanotificationrestapi.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Profile("prod")
public class KafkaConfig {
    @Value("${spring.kafka.consumer.group-id}")
    private String group;

    @Value("${spring.kafka.topic}")
    private String topic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Bean
    public KafkaSender<String, String> kafkaSender() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        SenderOptions<String, String> senderOptions = SenderOptions.create(producerProps);
        return KafkaSender.create(senderOptions);
    }

    @Bean
    public KafkaReceiver<String, String> kafkaReceiver() {


        Map<String, Object> consumerProps = new HashMap<>() {{
            this.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            this.put(ConsumerConfig.GROUP_ID_CONFIG, group);
            this.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            this.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            this.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        }};

        ReceiverOptions<String, String> receiverOptions = ReceiverOptions.<String, String>create(consumerProps)
                .subscription(Collections.singletonList(topic));
        return KafkaReceiver.create(receiverOptions);
    }
}
