package com.gkuznetsov.kafkanotificationrestapi.rest;

import com.gkuznetsov.kafkanotificationrestapi.dto.CreateNotificationRequestDto;
import com.gkuznetsov.kafkanotificationrestapi.mapper.NotificationMapper;
import com.gkuznetsov.kafkanotificationrestapi.service.ProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kafka-messages")
public class MessageProducerRestControllerV1 {
    private final ProducerService producerService;
    private final NotificationMapper notificationMapper;

    @Value("${spring.kafka.topic}")
    private String topic;

    @PostMapping
    public Mono<Void> sendNotification(@RequestBody @Valid CreateNotificationRequestDto requestDto) {
        return producerService.sendMessage( notificationMapper.map(requestDto) );
    }
}
