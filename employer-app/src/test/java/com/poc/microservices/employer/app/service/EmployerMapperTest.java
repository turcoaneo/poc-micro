package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EmployerMapperTest {

    private EmployerMapper employerMapper;

    @BeforeEach
    void setUp() {
        employerMapper = new EmployerMapper();
    }

    @Test
    void testConvertEmployerDTOToEntity() {
        EmployerDTO dto = new EmployerDTO(null,"TechCorp", List.of(new JobDTO("Developer", "Writes code", 50.0,
                List.of(new EmployeeDTO("Alice", 40)))));

        Employer result = employerMapper.toEntity(dto);

        assertEquals("TechCorp", result.getName());
        assertEquals(1, result.getJobs().size());
        assertTrue(result.getJobs().stream().map(Job::getTitle).anyMatch(x -> x.equals("Developer")));
        assertEquals(1, result.getEmployees().size());
        assertTrue(result.getJobs().stream().flatMap(job -> job.getAssignedEmployees().stream())
                .anyMatch(x -> x.getName().equals("Alice")));
    }

    @Test
    void testConvertEmployerDTOWithSharedEmployees() {
        JobDTO job1 = new JobDTO("Developer", "Writes code", 50.0, List.of(new EmployeeDTO("Alice", 40), new EmployeeDTO("Bob", 35)));
        JobDTO job2 = new JobDTO("Data Scientist", "Analyzes data", 55.0, List.of(new EmployeeDTO("Bob", 35), new EmployeeDTO("Charlie", 42)));

        EmployerDTO dto = new EmployerDTO(null, "TechCorp", List.of(job1, job2));

        Employer result = employerMapper.toEntity(dto);

        assertEquals("TechCorp", result.getName());
        assertEquals(2, result.getJobs().size());
        assertEquals(3, result.getEmployees().size()); // Alice, Bob, Charlie
        assertEquals(2, getJobByTitle(result, "Developer").getAssignedEmployees().size());
        assertEquals(2, getJobByTitle(result, "Data Scientist").getAssignedEmployees().size());
        assertEquals(2, getJobByEmployeeName(result, "Bob").getAssignedJobs().size());
        assertEquals(1, getJobByEmployeeName(result, "Charlie").getAssignedJobs().size());
    }

    @Test
    void testConvertEmployerEntityToDTO() {
        Employer employer = new Employer();
        employer.setId(1L);
        employer.setName("TechCorp");

        Job job1 = new Job();
        job1.setTitle("Developer");
        job1.setDescription("Writes code");
        job1.setHourRate(50.0);

        Employee employee1 = new Employee();
        employee1.setName("Alice");
        employee1.setWorkingHours(40);

        job1.getAssignedEmployees().add(employee1);
        employee1.getAssignedJobs().add(job1);

        employer.getJobs().add(job1);
        employer.getEmployees().add(employee1);

        EmployerDTO dto = employerMapper.toDTO(employer);

        assertEquals(1L, dto.getId());
        assertEquals("TechCorp", dto.getName());
        assertEquals(1, dto.getJobs().size());
        assertEquals("Developer", dto.getJobs().getFirst().getTitle());
        assertEquals(1, dto.getJobs().getFirst().getEmployees().size());
        assertEquals("Alice", dto.getJobs().getFirst().getEmployees().getFirst().getName());
    }



    private static Job getJobByTitle(Employer result, String jobTitle) {
        return result.getJobs().stream().filter(x -> x.getTitle().equals(jobTitle)).findAny().orElse(new Job());
    }

    private static Employee getJobByEmployeeName(Employer result, String employeeName) {
        return result.getEmployees().stream().filter(x -> x.getName().equals(employeeName)).findAny().orElse(new Employee());
    }
}