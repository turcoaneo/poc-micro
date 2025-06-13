package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.model.dto.EMGenericResponseDTO;
import com.poc.microservices.employer.app.model.dto.EmployeeDTO;
import com.poc.microservices.employer.app.model.dto.EmployerDTO;
import com.poc.microservices.employer.app.model.dto.EmployerEmployeeAssignmentPatchDTO;
import com.poc.microservices.employer.app.model.dto.JobDTO;
import com.poc.microservices.employer.app.repository.EmployeeRepository;
import com.poc.microservices.employer.app.repository.EmployerRepository;
import com.poc.microservices.employer.app.service.util.EmployeeMapper;
import com.poc.microservices.employer.app.service.util.EmployerMapper;
import com.poc.microservices.employer.app.service.util.JobMapper;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployerServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

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
    void testAssignEmployeeToJobs_success() {
        Long employerId = 1L;
        Long employeeId = 100L;

        Employer employer = new Employer(employerId, "Acme Corp", new HashSet<>());
        Job job1 = new Job(10L, "Dev", employer, "Java wizardry", 100.0, new HashSet<>());
        Job job2 = new Job(20L, "QA", employer, "Bug breaker", 80.0, new HashSet<>());
        employer.getJobs().addAll(List.of(job1, job2));

        Mockito.when(employerRepository.findById(employerId)).thenReturn(Optional.of(employer));

        EmployeeDTO employee = new EmployeeDTO(employeeId, "Alice");

        EmployerEmployeeAssignmentPatchDTO patchDTO = new EmployerEmployeeAssignmentPatchDTO();
        patchDTO.setEmployerId(employerId);
        patchDTO.setEmployee(employee);
        patchDTO.setJobIds(List.of(job1.getJobId()));

        EMGenericResponseDTO result = employerService.assignEmployeeToJobs(patchDTO);

        Assertions.assertEquals(employeeId, result.getId());
        Assertions.assertEquals("Added employee", result.getMessage());
        Assertions.assertTrue(job1.getEmployees().stream().anyMatch(e -> e.getEmployeeId().equals(employeeId)));
        Assertions.assertTrue(job2.getEmployees().isEmpty());

        Mockito.verify(employerRepository).save(Mockito.any());
        Mockito.verify(employeeRepository).save(Mockito.any());
    }

    @Test
    void testAssignEmployeeToJobs_invalidJobId() {
        Long employerId = 1L;
        Long employeeId = 100L;

        Employer employer = new Employer(employerId, "Acme Corp", new HashSet<>());
        Job job1 = new Job(10L, "Dev", employer, "Oops", 99.0, new HashSet<>());
        employer.getJobs().add(job1);

        Mockito.when(employerRepository.findById(employerId)).thenReturn(Optional.of(employer));

        EmployerEmployeeAssignmentPatchDTO patchDTO = new EmployerEmployeeAssignmentPatchDTO();
        patchDTO.setEmployerId(employerId);
        patchDTO.setEmployee(new EmployeeDTO(employeeId, "John"));
        patchDTO.setJobIds(List.of(42L)); // Invalid job ID

        EMGenericResponseDTO result = employerService.assignEmployeeToJobs(patchDTO);

        Assertions.assertEquals(42L, result.getId());
        Assertions.assertEquals("Job not found by this id", result.getMessage());

        Mockito.verify(employeeRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(employerRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void testAssignEmployeeToJobs_employerNotFound() {
        Long employerId = 999L;

        Mockito.when(employerRepository.findById(employerId)).thenReturn(Optional.empty());

        EmployerEmployeeAssignmentPatchDTO patchDTO = new EmployerEmployeeAssignmentPatchDTO();
        patchDTO.setEmployerId(employerId);
        patchDTO.setEmployee(new EmployeeDTO(100L, "Ghost"));
        patchDTO.setJobIds(List.of(1L));

        EMGenericResponseDTO result = employerService.assignEmployeeToJobs(patchDTO);

        Assertions.assertEquals(employerId, result.getId());
        Assertions.assertEquals("Employer not found", result.getMessage());

        Mockito.verify(employeeRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(employerRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void testCreateEmployer() {
        when(employerRepository.save(Mockito.any(Employer.class))).thenReturn(employer);

        Long result = employerService.createEmployer(employerMapper.toDTO(employer));

        assertNotNull(result);
        assertEquals(1L, result);
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
