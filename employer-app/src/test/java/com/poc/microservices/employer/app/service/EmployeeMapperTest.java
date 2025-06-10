package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.dto.EmployeeDTO;
import com.poc.microservices.employer.app.service.util.EmployeeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EmployeeMapperTest {

    private EmployeeMapper employeeMapper;

    @BeforeEach
    void setUp() {
        employeeMapper = new EmployeeMapper();
    }

    @Test
    void testConvertEmployeeEntityToDTO() {
        Employee employee = new Employee();
        employee.setName("Alice");
        employee.setWorkingHours(40);

        EmployeeDTO dto = employeeMapper.toDTO(employee);

        assertEquals("Alice", dto.getName());
        assertEquals(40, dto.getWorkingHours());
    }

    @Test
    void testConvertEmployeeDTOToEntity() {
        EmployeeDTO dto = new EmployeeDTO("Alice", 40);

        Employee result = employeeMapper.toEntity(dto);

        assertEquals("Alice", result.getName());
        assertEquals(40, result.getWorkingHours());
    }

}