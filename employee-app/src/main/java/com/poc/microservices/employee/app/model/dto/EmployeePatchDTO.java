package com.poc.microservices.employee.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeePatchDTO {
    private Long id;
    private String name; // if null we do not update
}
