package com.poc.microservices.main.app.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MASResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public MASResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
}