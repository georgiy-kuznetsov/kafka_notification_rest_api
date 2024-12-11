package com.gkuznetsov.kafkanotificationrestapi.exception;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(message, "NOT_FOUND");
    }
}
