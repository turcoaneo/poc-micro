package com.poc.microservices.main.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MASEmployeeDTO {
    private Long id = null;
    private String name;
    private Set<MASEmployeeEmployerDTO> employerDTOS;
    private Boolean active;
}