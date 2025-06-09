package com.poc.microservices.employer.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JobDTO {
    private String title;
    private String description;
    private Double hourRate;

    private List<EmployeeDTO> employees;
}