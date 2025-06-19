package com.poc.microservices.employer.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeJobDto {
    private Long employeeId;
    private String employeeName;
    private Long employerId;
    private String employerName;
    private Map<Long, String> jobIdToTitle;
}