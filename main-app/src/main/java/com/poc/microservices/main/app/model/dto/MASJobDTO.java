package com.poc.microservices.main.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MASJobDTO {
    private String title;
    private String description;
    private Double hourRate;
}