package com.gkuznetsov.kafkanotificationrestapi.util;

import com.gkuznetsov.kafkanotificationrestapi.dto.CreateNotificationRequestDto;
import com.gkuznetsov.kafkanotificationrestapi.dto.NotificationDto;
import com.gkuznetsov.kafkanotificationrestapi.entity.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class DataUtil {
    public static NotificationEntity getInternalNotificationEntityTransient() {
        return NotificationEntity.builder()
                .message("message 1")
                .messageType(NotificationMessageType.INTERNAL)
                .error("error 1")
                .userUid( UUID.randomUUID().toString() )
                .triggerCode(NotificationTriggerCode.DELETE_USER_2)
                .objectType(NotificationObjectType.MERCHANT)
                .objectId( UUID.randomUUID().toString() )
                .subject( UUID.randomUUID().toString() )
                .createdBy(NotificationCreator.SYSTEM)
                .hasConfirmOtp(true)
                .status(NotificationStatus.NEW)
                .expirationDate( LocalDateTime.now() )
                .createdAt( LocalDateTime.now() )
                .modifiedAt( LocalDateTime.now() )
                .build();
    }

    public static NotificationEntity getExternalNotificationEntityTransient() {
        return NotificationEntity.builder()
                .message("message 2")
                .messageType(NotificationMessageType.EXTERNAL)
                .error("error 2")
                .userUid(UUID.randomUUID().toString())
                .triggerCode(NotificationTriggerCode.USER_REGISTRATION_1)
                .objectType(NotificationObjectType.USER)
                .objectId( UUID.randomUUID().toString() )
                .subject( UUID.randomUUID().toString() )
                .createdBy(NotificationCreator.OPERATOR)
                .hasConfirmOtp(true)
                .status(NotificationStatus.NEW)
                .expirationDate( LocalDateTime.now() )
                .createdAt( LocalDateTime.now() )
                .modifiedAt( LocalDateTime.now() )
                .build();
    }

    public static CreateNotificationRequestDto getCreateInternalNotificationRequestDto() {
        NotificationEntity notificationEntity = getInternalNotificationEntityTransient();
        return getCreateInternalNotificationRequestDto(notificationEntity);
    }

    public static CreateNotificationRequestDto getCreateInternalNotificationRequestDto(NotificationEntity notificationEntity) {
        return CreateNotificationRequestDto.builder()
                .message( notificationEntity.getMessage() )
                .messageType( notificationEntity.getMessageType().toString() )
                .error( notificationEntity.getError() )
                .userUid( notificationEntity.getUserUid() )
                .triggerCode( notificationEntity.getTriggerCode().toString() )
                .objectType( notificationEntity.getObjectType().toString() )
                .objectId( notificationEntity.getObjectId() )
                .subject( notificationEntity.getSubject() )
                .createdBy( notificationEntity.getCreatedBy().toString() )
                .hasConfirmOtp( notificationEntity.isHasConfirmOtp() )
                .build();
    }

    public static NotificationDto getInternalNotificationDto() {
        NotificationEntity notificationEntity = getInternalNotificationEntityTransient();

        return NotificationDto.builder()
                .message( notificationEntity.getMessage() )
                .messageType( notificationEntity.getMessageType() )
                .error( notificationEntity.getError() )
                .userUid( notificationEntity.getUserUid() )
                .triggerCode( notificationEntity.getTriggerCode() )
                .objectType( notificationEntity.getObjectType() )
                .objectId( notificationEntity.getObjectId() )
                .subject( notificationEntity.getSubject() )
                .createdBy( notificationEntity.getCreatedBy() )
                .hasConfirmOtp( notificationEntity.isHasConfirmOtp() )
                .build();
    }

    public static NotificationDto getExternalNotificationDto() {
        NotificationEntity notificationEntity = getExternalNotificationEntityTransient();

        return NotificationDto.builder()
                .message( notificationEntity.getMessage() )
                .messageType( notificationEntity.getMessageType() )
                .error( notificationEntity.getError() )
                .userUid( notificationEntity.getUserUid() )
                .triggerCode( notificationEntity.getTriggerCode() )
                .objectType( notificationEntity.getObjectType() )
                .objectId( notificationEntity.getObjectId() )
                .subject( notificationEntity.getSubject() )
                .createdBy( notificationEntity.getCreatedBy() )
                .hasConfirmOtp( notificationEntity.isHasConfirmOtp() )
                .build();
    }
}
