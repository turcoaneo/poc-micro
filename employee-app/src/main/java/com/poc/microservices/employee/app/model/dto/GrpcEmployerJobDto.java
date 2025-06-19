package com.poc.microservices.employee.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GrpcEmployerJobDto {
    private Long employeeId;
    private String employeeName;
    private Long employerId;
    private String employerName;
    private Map<Long, String> jobIdToTitle;
}