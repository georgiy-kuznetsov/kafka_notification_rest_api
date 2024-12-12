package com.gkuznetsov.kafkanotificationrestapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gkuznetsov.kafkanotificationrestapi.dto.GetAllNotificationsResponseDto;
import com.gkuznetsov.kafkanotificationrestapi.dto.NotificationDto;
import com.gkuznetsov.kafkanotificationrestapi.dto.UpdateNotificationRequestDto;
import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationEntity;
import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationStatus;
import com.gkuznetsov.kafkanotificationrestapi.exception.ApiException;
import com.gkuznetsov.kafkanotificationrestapi.exception.NotFoundException;
import com.gkuznetsov.kafkanotificationrestapi.mapper.NotificationMapper;
import com.gkuznetsov.kafkanotificationrestapi.repository.NotificationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ObjectMapper objectMapper;
    private final KafkaReceiver<String, String> kafkaReceiver;

    public Mono<GetAllNotificationsResponseDto> getNotifications(int page, int pageSize) {
        int offset = (page - 1) * pageSize;

        return notificationRepository.findAllBy(offset, pageSize)
                .switchIfEmpty( Mono.empty() )
                .map(notificationMapper::map)
                .collectList()
                .zipWith( notificationRepository.count() )
                .map( tuples -> {
                    List<NotificationDto> notifications = tuples.getT1();
                    long notificationsTotalCount = tuples.getT2();

                    int totalPages = (int) Math.ceil( (double) notificationsTotalCount / pageSize );

                    if (totalPages > 0 && page > totalPages) {
                        throw new NotFoundException("Page not found");
                    }

                    return GetAllNotificationsResponseDto.builder()
                            .notifications(notifications)
                            .totalNotifications(notificationsTotalCount)
                            .pageSize(pageSize)
                            .currentPage(page)
                            .totalPages(totalPages)
                            .build();
                })
                .onErrorResume(Mono::error);
    }

    public Mono<NotificationEntity> getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .switchIfEmpty( Mono.error(new NotFoundException("Notification not found")) );
    }

    public Mono<NotificationEntity> updateNotificationById(Long notificationId, UpdateNotificationRequestDto requestDto) {
        NotificationStatus notificationStatus = NotificationStatus.valueOf( requestDto.getNotificationStatus() );

        return notificationRepository.findById(notificationId)
                .switchIfEmpty( Mono.error(new NotFoundException("Notification not found")) )
                .flatMap( notificationEntity -> {
                    notificationEntity.setStatus(notificationStatus);
                    notificationEntity.setModifiedAt( LocalDateTime.now() );
                    return notificationRepository.save(notificationEntity);
                });
    }

    @PostConstruct
    public void startListening() {
        kafkaReceiver.receive()
                .doOnNext(this::processMessage)
                .doOnError(e -> log.error("Error processing message: {}", e.getMessage()))
                .subscribe();
    }

    private void processMessage(ReceiverRecord<String, String> record) {
        String message = record.value();
        log.info("Received message: {}", message);

        NotificationEntity notificationEntity = parseMessage(message);
        notificationEntity.setStatus(NotificationStatus.NEW);
        notificationEntity.setExpirationDate( LocalDateTime.now().plusSeconds(86_400) );
        notificationEntity.setCreatedAt( LocalDateTime.now() );
        notificationEntity.setModifiedAt( LocalDateTime.now() );

        notificationRepository.save(notificationEntity).subscribe();
    }

    private NotificationEntity parseMessage(String message) {
        try {
            return objectMapper.readValue(message, NotificationEntity.class);
        } catch (JsonProcessingException e) {
            throw new ApiException("Error parsing notification from json", "JSON_PARSING_ERROR");
        }
    }
}
