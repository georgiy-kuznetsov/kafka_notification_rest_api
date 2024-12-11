package com.gkuznetsov.kafkanotificationrestapi.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gkuznetsov.kafkanotificationrestapi.dto.NotificationDto;
import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationEntity;
import com.gkuznetsov.kafkanotificationrestapi.exception.ApiException;
import com.gkuznetsov.kafkanotificationrestapi.service.KafkaProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kafka-messages")
public class KafkaMessageRestControllerV1 {
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @PostMapping
    public Mono<Void> sendNotification(@RequestBody @Valid NotificationDto notificationDto) {
        return Mono.fromCallable( () -> {
                    String notification;

                    try {
                        notification = objectMapper.writeValueAsString(notificationDto);
                    } catch (JsonProcessingException e) {
                        throw new ApiException("Error parsing notification from json", "JSON_PARSING_ERROR");
                    }

                    return notification;
                })
                .flatMap( notificationJson -> {
                    kafkaProducer.sendMessage("notification-topic", notificationJson);
                    return Mono.empty();
                })
                .doOnError( e -> log.error("IN sendNotification of KafkaMessageRestControllerV1: {}", e.getMessage()) )
                .then();
    }
}
