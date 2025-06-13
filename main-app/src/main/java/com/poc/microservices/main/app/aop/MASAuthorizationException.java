package com.poc.microservices.main.app.aop;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MASAuthorizationException extends RuntimeException {
    private final HttpStatus status;

    public MASAuthorizationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}