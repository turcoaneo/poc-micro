package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.model.dto.EmployeeDTO;
import com.poc.microservices.employer.app.model.dto.EmployerDTO;
import com.poc.microservices.employer.app.model.dto.JobDTO;
import com.poc.microservices.employer.app.repository.EmployerRepository;
import com.poc.microservices.employer.app.service.util.EmployeeMapper;
import com.poc.microservices.employer.app.service.util.EmployerMapper;
import com.poc.microservices.employer.app.service.util.JobMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployerServiceTest {

    @Mock
    private EmployerRepository employerRepository;

    @Spy
    private EmployerMapper employerMapper;
    @Spy
    private JobMapper jobMapper;
    @Spy
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployerService employerService;

    private Employer employer;

    @BeforeEach
    void setUp() {
        employer = new Employer();
        employer.setEmployerId(1L);
        employer.setName("TestCorp");

        assertNotNull(jobMapper);
        assertNotNull(employeeMapper);
    }

    @Test
    void testCreateEmployer() {
        when(employerRepository.save(Mockito.any(Employer.class))).thenReturn(employer);

        EmployerDTO result = employerService.createEmployer(employerMapper.toDTO(employer));

        assertNotNull(result);
        assertEquals("TestCorp", result.getName());
    }

    @Test
    void testUpdateEmployer_WhenExists() {
        Employer updated = new Employer();
        updated.setEmployerId(2L);
        updated.setName("UpdatedCorp");

        when(employerRepository.findById(2L)).thenReturn(Optional.of(employer));
        when(employerRepository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        EmployerDTO result = employerService.updateEmployer(employerMapper.toDTO(updated));

        assertEquals("UpdatedCorp", result.getName());
    }

    @Test
    void testGetEmployerByName() {
        when(employerRepository.findByName("TestCorp")).thenReturn(Optional.of(employer));

        Optional<EmployerDTO> result = employerService.getEmployerByName("TestCorp");

        assertTrue(result.isPresent());
        assertEquals("TestCorp", result.get().getName());
    }

    @Test
    void testGetJobsByEmployerId() {
        Job job = getJob();
        employer.setJobs(Set.of(job));

        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer));

        Set<JobDTO> jobs = employerService.getJobsByEmployerId(1L);

        assertEquals(1, jobs.size());
    }

    @Test
    void testGetEmployeesByEmployerId() {
        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("Alice");
        Job job = getJob();
        job.getEmployees().add(employee);
        employer.setJobs(Set.of(job));

        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer));

        Set<EmployeeDTO> employees = employerService.getEmployeesByEmployerId(1L);

        assertEquals(1, employees.size());
    }

    private static Job getJob() {
        Job job = new Job();
        job.setTitle("Engineer");
        job.setJobId(1L);
        return job;
    }
}
