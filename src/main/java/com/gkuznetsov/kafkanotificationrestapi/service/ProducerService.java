package com.gkuznetsov.kafkanotificationrestapi.service;

import com.gkuznetsov.kafkanotificationrestapi.dto.NotificationDto;
import com.gkuznetsov.kafkanotificationrestapi.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerService {
    private final KafkaUtil kafkaUtil;

    public Mono<Void> sendMessage(NotificationDto notificationDto) {
        return Mono.just(notificationDto)
                .flatMap(kafkaUtil::send)
                .doOnError( e -> log.error("IN sendMessage of ProducerService: {}", e.getMessage()) )
                .then();
    }
}
