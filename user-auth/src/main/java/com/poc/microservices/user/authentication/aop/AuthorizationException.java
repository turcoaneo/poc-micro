package com.poc.microservices.user.authentication.aop;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthorizationException extends RuntimeException {
    private final HttpStatus status;

    public AuthorizationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}