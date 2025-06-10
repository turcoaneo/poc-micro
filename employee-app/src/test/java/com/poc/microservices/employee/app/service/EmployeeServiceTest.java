package com.poc.microservices.employee.app.service;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.dto.EmployeeDTO;
import com.poc.microservices.employee.app.repository.EmployeeRepository;
import com.poc.microservices.employee.app.service.util.EmployeeMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;


@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        Assertions.assertNotNull(employeeMapper);
    }

    @Test
    void testCreateEmployee() {
        EmployeeDTO dto = new EmployeeDTO("Alice", 40);
        Employee entity = new Employee(null, "Alice", 40, new HashSet<>());

        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(entity);

        EmployeeDTO result = employeeService.createEmployee(dto);

        Assertions.assertEquals("Alice", result.getName());
        Assertions.assertEquals(40, result.getWorkingHours());
    }
}