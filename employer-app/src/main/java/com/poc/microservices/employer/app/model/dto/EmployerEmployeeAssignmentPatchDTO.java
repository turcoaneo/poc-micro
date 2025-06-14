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
public class EmployerEmployeeAssignmentPatchDTO {
    private Long employerId;
    private EmployeeDTO employee;
    private List<Long> jobIds;
}