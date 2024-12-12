package com.gkuznetsov.kafkanotificationrestapi.mapper;

import com.gkuznetsov.kafkanotificationrestapi.dto.CreateNotificationRequestDto;
import com.gkuznetsov.kafkanotificationrestapi.dto.NotificationDto;
import com.gkuznetsov.kafkanotificationrestapi.entity.*;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public NotificationDto map(NotificationEntity notification) {
        return NotificationDto.builder()
                .id( notification.getId() )
                .message( notification.getMessage() )
                .messageType( notification.getMessageType() )
                .error( notification.getError() )
                .userUid( notification.getUserUid() )
                .status( notification.getStatus() )
                .triggerCode( notification.getTriggerCode() )
                .objectType( notification.getObjectType() )
                .objectId( notification.getObjectId() )
                .subject( notification.getSubject() )
                .createdBy( notification.getCreatedBy() )
                .hasConfirmOtp( notification.isHasConfirmOtp() )
                .expirationDate( notification.getExpirationDate() )
                .createdAt( notification.getCreatedAt() )
                .modifiedAt( notification.getModifiedAt() )
                .build();
    }

    public NotificationDto map(CreateNotificationRequestDto requestDto) {
        NotificationMessageType messageType = NotificationMessageType.valueOf( requestDto.getMessageType() );
        NotificationTriggerCode triggerCode = NotificationTriggerCode.valueOf( requestDto.getTriggerCode() );
        NotificationObjectType objectType = NotificationObjectType.valueOf( requestDto.getObjectType() );
        NotificationCreator notificationCreator = NotificationCreator.valueOf( requestDto.getCreatedBy() );

        return NotificationDto.builder()
                .message( requestDto.getMessage() )
                .messageType(messageType)
                .error( requestDto.getError() )
                .userUid( requestDto.getUserUid() )
                .triggerCode(triggerCode)
                .objectType(objectType)
                .objectId( requestDto.getObjectId() )
                .subject( requestDto.getSubject() )
                .createdBy(notificationCreator)
                .hasConfirmOtp( requestDto.getHasConfirmOtp() )
                .build();
    }
}
