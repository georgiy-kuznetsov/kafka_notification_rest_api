package com.gkuznetsov.kafkanotificationrestapi.util;

import com.gkuznetsov.kafkanotificationrestapi.dto.CreateNotificationRequestDto;
import com.gkuznetsov.kafkanotificationrestapi.entity.*;

import java.util.UUID;

public class DataUtil {
    public static NotificationEntity getInternalNotificationEntityTransient() {
        return NotificationEntity.builder()
                .message("message 1")
                .messageType( NotificationMessageType.INTERNAL.name() )
                .error("error 1")
                .userUid(UUID.randomUUID().toString())
                .triggerCode( NotificationTriggerCode.DELETE_USER_2.name() )
                .objectType( NotificationObjectType.MERCHANT.name() )
                .subject(UUID.randomUUID().toString())
                .createdBy( NotificationCreator.SYSTEM.name() )
                .hasConfirmOtp(true)
                .build();
    }

    public static NotificationEntity getExternalNotificationEntityTransient() {
        return NotificationEntity.builder()
                .message("message 2")
                .messageType( NotificationMessageType.EXTERNAL.name() )
                .error("error 2")
                .userUid(UUID.randomUUID().toString())
                .triggerCode( NotificationTriggerCode.USER_REGISTRATION_1.name() )
                .objectType( NotificationObjectType.USER.name() )
                .subject( UUID.randomUUID().toString() )
                .createdBy( NotificationCreator.OPERATOR.name() )
                .hasConfirmOtp(true)
                .build();
    }

    public static CreateNotificationRequestDto getCreateNotificationRequestDto() {
        return CreateNotificationRequestDto.builder()
                .message("message 2")
                .messageType( NotificationMessageType.EXTERNAL.toString() )
                .error("error 2")
                .userUid(UUID.randomUUID().toString())
                .triggerCode( NotificationTriggerCode.USER_REGISTRATION_1.toString() )
                .objectType( NotificationObjectType.USER.toString() )
                .subject( UUID.randomUUID().toString() )
                .createdBy( NotificationCreator.OPERATOR.toString() )
                .hasConfirmOtp(true)
                .build();
    }
}
