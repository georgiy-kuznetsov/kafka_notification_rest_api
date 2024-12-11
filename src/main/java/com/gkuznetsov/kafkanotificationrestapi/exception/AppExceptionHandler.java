package com.gkuznetsov.kafkanotificationrestapi.exception;

import com.gkuznetsov.kafkanotificationrestapi.dto.ErrorFieldsValidationResponseDto;
import com.gkuznetsov.kafkanotificationrestapi.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(new ErrorResponseDto(e.getMessage(), e.getErrorCode()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorFieldsValidationResponseDto> handleFieldsValidationExceptions(WebExchangeBindException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult()
                .getFieldErrors()
                .forEach( error -> errors.put(error.getField(), error.getDefaultMessage()) );

        return new ResponseEntity<>(
                new ErrorFieldsValidationResponseDto("FIELDS_VALIDATION_ERROR", errors),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponseDto> handleApiException(ApiException e) {
        return new ResponseEntity<>(new ErrorResponseDto(e.getMessage(), e.getErrorCode()), HttpStatus.CONFLICT);
    }
}
