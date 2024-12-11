package com.gkuznetsov.kafkanotificationrestapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorFieldsValidationResponseDto {
    private String errorCode;
    private Map<String, String> errors;
}
