package com.poc.microservices.user.authentication.aop;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UAMAuthorizationException extends RuntimeException {
    private final HttpStatus status;

    public UAMAuthorizationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}