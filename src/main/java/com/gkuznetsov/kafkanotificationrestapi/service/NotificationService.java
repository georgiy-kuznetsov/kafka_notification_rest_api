package com.gkuznetsov.kafkanotificationrestapi.service;

import com.gkuznetsov.kafkanotificationrestapi.dto.GetAllNotificationsResponseDto;
import com.gkuznetsov.kafkanotificationrestapi.dto.NotificationDto;
import com.gkuznetsov.kafkanotificationrestapi.dto.UpdateNotificationRequestDto;
import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationEntity;
import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationStatus;
import com.gkuznetsov.kafkanotificationrestapi.exception.NotFoundException;
import com.gkuznetsov.kafkanotificationrestapi.mapper.NotificationMapper;
import com.gkuznetsov.kafkanotificationrestapi.repository.NotificationRepository;
import com.gkuznetsov.kafkanotificationrestapi.util.KafkaUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final KafkaUtil kafkaUtil;

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
        kafkaUtil.receive()
                .flatMap( notificationEntity -> {
                    notificationEntity.setStatus(NotificationStatus.NEW);
                    notificationEntity.setExpirationDate( LocalDateTime.now().plusSeconds(86_400) );
                    notificationEntity.setCreatedAt( LocalDateTime.now() );
                    notificationEntity.setModifiedAt( LocalDateTime.now() );

                    return notificationRepository.existsByUniqueFields(
                                    notificationEntity.getObjectId(),
                                    notificationEntity.getStatus().name(),
                                    notificationEntity.getMessageType().name(),
                                    notificationEntity.getTriggerCode().name()
                            )
                            .flatMap(exists -> {
                                if (exists) {
                                    log.error("IN processMessage add exists notification: {}", notificationEntity);
                                    return Mono.empty();
                                }

                                return notificationRepository.save(notificationEntity);
                            })
                            .onErrorResume(e -> {
                                log.error("Error processing message: {}", e.getMessage());
                                return Mono.empty();
                            });
                })
                .subscribe();
    }
}
