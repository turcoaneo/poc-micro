package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.dto.EmployeeDTO;
import com.poc.microservices.employer.app.model.dto.EmployerDTO;
import com.poc.microservices.employer.app.model.dto.JobDTO;
import com.poc.microservices.employer.app.service.util.EmployerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EmployerMapperTest {

    private EmployerMapper employerMapper;

    @BeforeEach
    void setUp() {
        employerMapper = new EmployerMapper();
    }

    @Test
    void testConvertEmployerDTOToEntity() {
        EmployerDTO dto = new EmployerDTO("TechCorp", List.of(new JobDTO("Developer", "Writes code", 50.0)), List.of(new EmployeeDTO("Alice", 40)));

        Employer result = employerMapper.toEntity(dto);

        assertEquals("TechCorp", result.getName());
        assertEquals(1, result.getJobs().size());
        assertEquals("Developer", result.getJobs().getFirst().getTitle());
        assertEquals(1, result.getEmployees().size());
        assertEquals("Alice", result.getEmployees().getFirst().getName());
    }
}