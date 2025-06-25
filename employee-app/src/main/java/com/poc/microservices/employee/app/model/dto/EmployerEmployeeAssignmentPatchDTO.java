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
public class EmployerEmployeeAssignmentPatchDTO {
    private Long employerId;
    private EmployeePatchDTO employee;
    private Map<Long, Integer> jobIdWorkingHoursMap;
}
