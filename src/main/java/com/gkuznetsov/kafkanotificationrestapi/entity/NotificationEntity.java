package com.gkuznetsov.kafkanotificationrestapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    private Long id;

    private String message;

    @Column("message_type")
    private NotificationMessageType messageType;

    private String error;

    @Column("user_uid")
    private String userUid;

    @Column("notification_status")
    private NotificationStatus status;

    @Column("trigger_code")
    private NotificationTriggerCode triggerCode;

    @Column("object_type")
    private NotificationObjectType objectType;

    @Column("object_id")
    private String objectId;

    private String subject;

    @Column("created_by")
    private NotificationCreator createdBy;

    @Column("has_confirm_otp")
    private boolean hasConfirmOtp;

    @Column("expiration_date")
    private LocalDateTime expirationDate;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("modified_at")
    private LocalDateTime modifiedAt;
}
