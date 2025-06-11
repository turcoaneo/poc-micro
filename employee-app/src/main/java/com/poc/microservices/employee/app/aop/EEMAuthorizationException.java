package com.poc.microservices.employee.app.aop;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EEMAuthorizationException extends RuntimeException {
    private final HttpStatus status;

    public EEMAuthorizationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}