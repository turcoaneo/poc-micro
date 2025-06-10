package com.poc.microservices.employer.app.aop;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EMAuthorizationException extends RuntimeException {
    private final HttpStatus status;

    public EMAuthorizationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}