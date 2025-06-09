package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.repository.EmployerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployerServiceTest {

    @Mock
    private EmployerRepository employerRepository;

    @InjectMocks
    private EmployerService employerService;

    private Employer employer;

    @BeforeEach
    void setUp() {
        employer = new Employer();
        employer.setId(1L);
        employer.setName("TestCorp");
    }

    @Test
    void testCreateEmployer() {
        when(employerRepository.save(Mockito.any(Employer.class))).thenReturn(employer);

        Employer result = employerService.createEmployer(employer);

        assertNotNull(result);
        assertEquals("TestCorp", result.getName());
        verify(employerRepository).save(employer);
    }

    @Test
    void testUpdateEmployer_WhenExists() {
        Employer updated = new Employer();
        updated.setName("UpdatedCorp");

        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer));
        when(employerRepository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        Employer result = employerService.updateEmployer(1L, updated);

        assertEquals("UpdatedCorp", result.getName());
    }

    @Test
    void testGetEmployerByName() {
        when(employerRepository.findByName("TestCorp")).thenReturn(Optional.of(employer));

        Optional<Employer> result = employerService.getEmployerByName("TestCorp");

        assertTrue(result.isPresent());
        assertEquals("TestCorp", result.get().getName());
    }

    @Test
    void testGetJobsByEmployerId() {
        Job job = new Job();
        job.setTitle("Engineer");
        employer.setJobs(List.of(job));

        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer));

        List<Job> jobs = employerService.getJobsByEmployerId(1L);

        assertEquals(1, jobs.size());
    }

    @Test
    void testGetEmployeesByEmployerId() {
        Employee employee = new Employee();
        employee.setName("Alice");
        employer.setEmployees(List.of(employee));

        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer));

        List<Employee> employees = employerService.getEmployeesByEmployerId(1L);

        assertEquals(1, employees.size());
    }
}
