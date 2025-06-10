package com.poc.microservices.employee.app.service;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.dto.EmployeeDTO;
import com.poc.microservices.employee.app.model.dto.EmployerDTO;
import com.poc.microservices.employee.app.model.dto.JobDTO;
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

import java.util.List;


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
    void testCreateEmployeeWithEmployersAndJobs() {
        EmployeeDTO dto = new EmployeeDTO("Alice", 40, List.of(
                new EmployerDTO(1L, "TechCorp", List.of(new JobDTO(101L, "Developer"), new JobDTO(102L, "Architect"))),
                new EmployerDTO(2L, "DataLabs", List.of(new JobDTO(201L, "Analyst"), new JobDTO(202L, "ML Engineer")))
        ));

        Employee mappedEntity = employeeMapper.toEntity(dto);

        Employee entity = new Employee(null, "Alice", 40, mappedEntity.getJobEmployers());

        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(entity);

        EmployeeDTO result = employeeService.createEmployee(dto);

        Assertions.assertEquals("Alice", result.getName());
        Assertions.assertEquals(40, result.getWorkingHours());
        Assertions.assertEquals(2, result.getEmployers().size()); // Two employers
        Assertions.assertEquals(2, result.getEmployers().getFirst().getJobs().size()); // Two jobs at first employer
    }

}