package com.gkuznetsov.kafkanotificationrestapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gkuznetsov.kafkanotificationrestapi.dto.NotificationDto;
import com.gkuznetsov.kafkanotificationrestapi.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerService {
    private final KafkaSender<String, String> kafkaSender;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic}")
    private String topic;

    public Mono<Void> sendMessage(NotificationDto notificationDto) {
        return Mono.just(notificationDto)
                .map(this::parseMessage)
                .flatMap( notificationJson -> {
                    ProducerRecord<String, String> record = new ProducerRecord<>(topic, notificationJson);

                    return kafkaSender
                            .send( Mono.just(SenderRecord.create(record, null)) )
                            .then();
                })
                .doOnError( e -> log.error("IN sendMessage of ProducerService: {}", e.getMessage()) )
                .then();
    }

    private String parseMessage(NotificationDto notificationDto) {
        try {
            return objectMapper.writeValueAsString(notificationDto);
        } catch (JsonProcessingException e) {
            throw new ApiException("Error parsing notification from json", "JSON_PARSING_ERROR");
        }
    }
}
