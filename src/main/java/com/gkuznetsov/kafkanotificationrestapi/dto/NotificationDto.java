package com.gkuznetsov.kafkanotificationrestapi.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.gkuznetsov.kafkanotificationrestapi.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NotificationDto {
    private Long id;
    private String message;
    private NotificationMessageType messageType;
    private String error;
    private String userUid;
    private NotificationTriggerCode triggerCode;
    private NotificationObjectType objectType;
    private String objectId;
    private String subject;
    private NotificationCreator createdBy;
    private boolean hasConfirmOtp;
    private NotificationStatus status;
    private LocalDateTime expirationDate;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
