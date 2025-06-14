package com.poc.microservices.employer.app.service.util;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.dto.EmployeeDTO;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class EmployeeMapper {

    public EmployeeDTO toDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getEmployeeId());
        dto.setName(employee.getName());
        return dto;
    }

    public Employee toEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setEmployeeId(dto.getId());
        return employee;
    }
}