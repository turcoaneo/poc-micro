package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.model.dto.EmployeeJobDto;
import com.poc.microservices.employer.app.repository.EmployeeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class EmployeeJobServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeJobService employeeJobService;

    @BeforeEach
    void setUp() {
        employeeJobService = new EmployeeJobService(employeeRepository);
    }

    @Test
    void testGetEmployeeJobInfo() {
        // Mock database entities
        Employee employee = new Employee();
        employee.setEmployeeId(1001L);
        employee.setName("Alice");

        Employer employer = new Employer();
        employer.setEmployerId(5001L);
        employer.setName("TechCorp");

        Job job1 = new Job();
        job1.setJobId(2001L);
        job1.setTitle("Developer");
        job1.setEmployer(employer);

        Job job2 = new Job();
        job2.setJobId(2002L);
        job2.setTitle("Architect");
        job2.setEmployer(employer);

        employee.getJobs().addAll(Set.of(job1, job2));

        Mockito.when(employeeRepository.findEmployeesByEmployeeIdIn(List.of(1001L))).thenReturn(List.of(employee));

        // Execute service method
        List<EmployeeJobDto> result = employeeJobService.getEmployeeJobInfo(List.of(1001L));

        // Validate output
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1001L, result.getFirst().getEmployeeId());
        Assertions.assertEquals(5001L, result.getFirst().getEmployerId());
        Assertions.assertEquals(
                Stream.of(2001L, 2002L).sorted().toList(),
                result.getFirst().getJobIdToTitle().keySet().stream().sorted().toList()
        );
    }
}