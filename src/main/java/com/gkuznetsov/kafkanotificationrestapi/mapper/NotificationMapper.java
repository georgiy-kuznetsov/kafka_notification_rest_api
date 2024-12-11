package com.gkuznetsov.kafkanotificationrestapi.mapper;

import com.gkuznetsov.kafkanotificationrestapi.dto.NotificationDto;
import com.gkuznetsov.kafkanotificationrestapi.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationDto map(NotificationEntity notification) {
        return NotificationDto.builder()
                .message( notification.getMessage() )
                .messageType( notification.getMessageType() )
                .error( notification.getError() )
                .userUid( notification.getUserUid() )
                .notificationStatus( notification.getStatus() )
                .triggerCode( notification.getTriggerCode() )
                .objectType( notification.getObjectType() )
                .objectId( notification.getObjectId() )
                .subject( notification.getSubject() )
                .createdBy( notification.getCreatedBy() )
                .hasConfirmOtp( notification.isHasConfirmOtp() )
                .createdAt( notification.getCreatedAt() )
                .modifiedAt( notification.getModifiedAt() )
                .build();
    }
}
