package com.gkuznetsov.kafkanotificationrestapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gkuznetsov.kafkanotificationrestapi.dto.NotificationDto;
import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationEntity;
import com.gkuznetsov.kafkanotificationrestapi.exception.ApiException;
import com.gkuznetsov.kafkanotificationrestapi.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Component
@RequiredArgsConstructor
public class KafkaUtil {
    private final KafkaReceiver<String, String> kafkaReceiver;
    private final KafkaSender<String, String> kafkaSender;

    private final NotificationMapper notificationMapper;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic}")
    private String topic;

    public Flux<NotificationEntity> receive() {
        return kafkaReceiver.receive()
                .flatMap( record -> {
                    String message = record.value();
                    return Mono.just( parseMessage(message) );
                });
    }

    public Mono<Void> send(NotificationDto notificationDto) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, parseMessage(notificationDto));

        return kafkaSender
                .send( Mono.just(SenderRecord.create(record, null)) ).then();
    }

    private String parseMessage(NotificationDto notificationDto) {
        try {
            return objectMapper.writeValueAsString(notificationDto);
        } catch (JsonProcessingException e) {
            throw new ApiException("Error parsing notification from json", "JSON_PARSING_ERROR");
        }
    }

    private NotificationEntity parseMessage(String message) {
        try {
            return notificationMapper.map( objectMapper.readValue(message, NotificationDto.class) );
        } catch (JsonProcessingException e) {
            throw new ApiException("Error parsing notification from json", "JSON_PARSING_ERROR");
        }
    }
}
