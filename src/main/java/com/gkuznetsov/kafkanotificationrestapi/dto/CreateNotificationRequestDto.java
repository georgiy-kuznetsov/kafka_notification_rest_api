package com.gkuznetsov.kafkanotificationrestapi.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateNotificationRequestDto {
    @NotBlank(message = "The message should not be empty")
    private String message;

    @NotBlank(message = "The message_type should not be empty")
    @Pattern(regexp = "INTERNAL|EXTERNAL", message = "The type of message can only be changed to INTERNAL or EXTERNAL")
    private String messageType;

    private String error;

    @Length(max = 36, message = "The user's UID must be no longer than 36 characters")
    private String userUid;

    @NotBlank(message = "The trigger_code should not be empty")
    @Pattern(regexp = "USER_REGISTRATION_1|DELETE_USER_2", message = "The trigger code can only be changed to USER_REGISTRATION_1 or DELETE_USER_2")
    private String triggerCode;

    @NotBlank(message = "The object_type should not be empty")
    @Pattern(regexp = "USER|PAYMENT|MERCHANT", message = "The type of object can only be changed to USER, PAYMENT or MERCHANT")
    private String objectType;

    @Length(max = 36, message = "The object`s id must be no longer than 36 characters")
    private String objectId;

    @Length(max = 128, message = "The subject field must be no longer than 128 characters")
    private String subject;

    @NotBlank(message = "The created_by should not be empty")
    @Pattern(regexp = "OPERATOR|SYSTEM", message = "The created_by field must have the value OPERATOR or SYSTEM")
    private String createdBy;

    @NotNull(message = "The OTP confirmation cannot be null.")
    @AssertTrue(message = "The OTP confirmation must be true.")
    private Boolean hasConfirmOtp;
}
