package com.poc.microservices.main.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MASEmployerEmployeeAssignmentPatchDTO {
    private Long employerId;
    private MASEmployeeDTO employee;
    private List<Long> jobIds;
}