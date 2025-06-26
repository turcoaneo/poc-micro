package com.poc.microservices.employer.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EMWorkingHoursResponseDTO {
    private Long employerId;
    private Set<EMJobWorkingHoursDTO> jobWorkingHoursDTOS;
}
