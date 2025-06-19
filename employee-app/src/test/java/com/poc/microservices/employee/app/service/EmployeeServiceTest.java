package com.poc.microservices.employee.app.service;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.Employer;
import com.poc.microservices.employee.app.model.Job;
import com.poc.microservices.employee.app.model.dto.EmployeeDTO;
import com.poc.microservices.employee.app.model.dto.EmployerDTO;
import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDto;
import com.poc.microservices.employee.app.model.dto.JobDTO;
import com.poc.microservices.employee.app.repository.EmployeeJobEmployerRepository;
import com.poc.microservices.employee.app.repository.EmployeeRepository;
import com.poc.microservices.employee.app.repository.EmployerRepository;
import com.poc.microservices.employee.app.repository.JobRepository;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployerRepository employerRepository;


    @Mock
    private JobRepository jobRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private EmployeeMapper employeeMapper;

    @Mock
    private EmployeeJobEmployerRepository employeeJobEmployerRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        Assertions.assertNotNull(employeeMapper);
        Assertions.assertNotNull(jobRepository);
        Assertions.assertNotNull(employerRepository);
    }

    @Test
    void testUpdateEmployeeWithNames() {
        long employeeId = 9;
        String employeeName = "Alice";
        long employerId = 1;
        String employerName = "TechCorp";
        List<Integer> jobIds = List.of(11, 12);
        List<String> jobTitles = List.of("Developer", "Data Scientist");


        HashMap<Long, String> jobIdToTitle = new HashMap<>();
        jobIdToTitle.put(11L, "Job 1");
        jobIdToTitle.put(12L, "Job 2");

        GrpcEmployerJobDto dto = new GrpcEmployerJobDto(employeeId, "Employee", employerId, "Employer", jobIdToTitle);

        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setName(employeeName);
        employee.setJobEmployers(new HashSet<>());

        Mockito.when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        Employer savedEmployer = new Employer(null, employerId, employerName, new HashSet<>());
        Mockito.when(employerRepository.save(Mockito.any(Employer.class))).thenReturn(savedEmployer);

        for (int i = 0; i < jobIds.size(); i++) {
            long jobId = jobIds.get(i);
            String title = jobTitles.get(i);

            Job savedJob = new Job(null, jobId, title, new HashSet<>());
            Mockito.when(jobRepository.save(Mockito.argThat(j -> j != null && jobId == j.getJobId()))).thenReturn(savedJob);
        }

        employeeService.updateEmployee(dto);

        Mockito.verify(employeeRepository).save(employee);
        Mockito.verify(employeeJobEmployerRepository).saveAll(Mockito.any());
    }


    @Test
    void testCreateEmployeeWithEmployersAndJobs() {
        EmployeeDTO dto = new EmployeeDTO(null, "Alice", 40, List.of(
                new EmployerDTO(1L, "TechCorp", List.of(new JobDTO(101L, "Developer"), new JobDTO(102L, "Architect"))),
                new EmployerDTO(2L, "DataLabs", List.of(new JobDTO(201L, "Analyst"), new JobDTO(202L, "ML Engineer")))
        ));

        Employee mappedEntity = employeeMapper.toEntity(dto);

        Employee entity = new Employee(0L, "Alice", 40, mappedEntity.getJobEmployers());

        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(entity);

        Long employeeId = employeeService.createEmployee(dto);

        Assertions.assertEquals(0L, employeeId);
    }

    @Test
    void testGetEmployeesByJobId() {
        List<Employee> employees = List.of(new Employee(null, "Alice", 40, new HashSet<>()),
                new Employee(null, "Bob", 35, new HashSet<>()));

        Mockito.when(employeeJobEmployerRepository.findEmployeesByJobId(101L)).thenReturn(employees);

        List<EmployeeDTO> result = employeeService.getEmployeesByJobId(101L);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Alice", result.get(0).getName());
        Assertions.assertEquals("Bob", result.get(1).getName());
    }

    @Test
    void testGetEmployeesByEmployerId() {
        List<Employee> employees = List.of(new Employee(null, "Charlie", 42, new HashSet<>()));

        Mockito.when(employeeJobEmployerRepository.findEmployeesByEmployerId(1L)).thenReturn(employees);

        List<EmployeeDTO> result = employeeService.getEmployeesByEmployerId(1L);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Charlie", result.getFirst().getName());
    }


}