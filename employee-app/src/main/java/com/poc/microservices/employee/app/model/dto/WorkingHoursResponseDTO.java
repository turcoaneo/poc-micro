package com.poc.microservices.employee.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WorkingHoursResponseDTO {
    private Long employeeId;
    private Long employerId;
    private Set<JobWorkingHoursDTO> jobWorkingHoursDTOS;
}
