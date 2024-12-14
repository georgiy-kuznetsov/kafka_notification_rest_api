package com.gkuznetsov.kafkanotificationrestapi.repository;

import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationRepository extends R2dbcRepository<NotificationEntity, Long> {
    @Query("SELECT * FROM notifications LIMIT :limit OFFSET :offset")
    Flux<NotificationEntity> findAllBy(@Param("offset") int offset, @Param("limit") int limit);

    @Query("SELECT COUNT(*) > 0 FROM notifications " +
            "WHERE object_id = :objectId " +
            "AND notification_status = :notificationStatus " +
            "AND message_type = :messageType " +
            "AND trigger_code = :triggerCode")
    Mono<Boolean> existsByUniqueFields(@Param("objectId") String objectId,
                                       @Param("notificationStatus") String notificationStatus,
                                       @Param("messageType") String messageType,
                                       @Param("triggerCode") String triggerCode);
}
