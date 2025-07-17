package com.poc.microservices.employee.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployerDTO {
    private Long employerId;
    private String name;
    private List<JobDTO> jobDTOS;
}