package com.gkuznetsov.kafkanotificationrestapi.repository;

import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationRepository extends ReactiveSortingRepository<NotificationEntity, Long> {
    @Query("SELECT * FROM notifications LIMIT :limit OFFSET :offset")
    Flux<NotificationEntity> findAllBy(@Param("offset") int offset, @Param("limit") int limit);

    Mono<NotificationEntity> findById(Long notificationId);

    Mono<Long> count();

    Mono<NotificationEntity> save(NotificationEntity notification);
}
