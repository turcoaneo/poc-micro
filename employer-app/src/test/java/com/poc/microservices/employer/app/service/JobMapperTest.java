package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.model.dto.EmployeeDTO;
import com.poc.microservices.employer.app.model.dto.JobDTO;
import com.poc.microservices.employer.app.service.util.JobMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class JobMapperTest {

    private JobMapper jobMapper;

    @BeforeEach
    void setUp() {
        jobMapper = new JobMapper();
    }

    @Test
    void testConvertJobEntityToDTO() {
        Job job = new Job();
        job.setTitle("Developer");
        job.setDescription("Writes code");
        job.setHourRate(50.0);

        Employee employee = new Employee();
        employee.setName("Alice");
        employee.setWorkingHours(40);
        job.getAssignedEmployees().add(employee);

        JobDTO dto = jobMapper.toDTO(job);

        assertEquals("Developer", dto.getTitle());
        assertEquals(50.0, dto.getHourRate());
        assertEquals(1, dto.getEmployees().size());
        assertEquals("Alice", dto.getEmployees().getFirst().getName());
    }

    @Test
    void testConvertJobDTOToEntity() {
        JobDTO dto = new JobDTO("Developer", "Writes code", 50.0, List.of(new EmployeeDTO("Alice", 40)));

        Job result = jobMapper.toEntity(dto);

        assertEquals("Developer", result.getTitle());
        assertEquals("Writes code", result.getDescription());
        assertEquals(50.0, result.getHourRate());
        assertEquals(1, result.getAssignedEmployees().size());
        assertEquals("Alice", result.getAssignedEmployees().getFirst().getName());
    }


}