package com.gkuznetsov.kafkanotificationrestapi.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateNotificationRequestDto {
    @NotBlank(message = "The status should not be empty")
    @Pattern(regexp = "COMPLETE", message = "The status can only be changed to COMPLETE")
    private String notificationStatus;
}
