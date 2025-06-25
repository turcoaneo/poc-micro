package com.poc.microservices.employer.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EMJobWorkingHoursDTO {
    private Long jobId;
    private Long employeeId;
    private Integer workingHours;
}