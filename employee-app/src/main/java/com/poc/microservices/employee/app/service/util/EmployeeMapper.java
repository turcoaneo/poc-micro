package com.poc.microservices.employee.app.service.util;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.dto.EmployeeDTO;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class EmployeeMapper {

    public Employee toEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setWorkingHours(dto.getWorkingHours());
        return employee;
    }

    public EmployeeDTO toDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setName(employee.getName());
        dto.setWorkingHours(employee.getWorkingHours());
        return dto;
    }
}