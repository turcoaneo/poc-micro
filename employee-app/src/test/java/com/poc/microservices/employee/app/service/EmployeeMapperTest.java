package com.poc.microservices.employee.app.service;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.EmployeeJobEmployer;
import com.poc.microservices.employee.app.model.Employer;
import com.poc.microservices.employee.app.model.Job;
import com.poc.microservices.employee.app.model.dto.EmployeeDTO;
import com.poc.microservices.employee.app.model.dto.EmployerDTO;
import com.poc.microservices.employee.app.model.dto.JobDTO;
import com.poc.microservices.employee.app.service.util.EmployeeMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class EmployeeMapperTest {

    @Spy
    private EmployeeMapper employeeMapper;

    @Test
    void testToEntity() {
        EmployeeDTO dto = new EmployeeDTO(null, "Alice", List.of(
                new EmployerDTO(1L, "TechCorp", List.of(new JobDTO(101L, "Developer", 40),
                        new JobDTO(102L, "Architect", 40))),
                new EmployerDTO(2L, "DataLabs", List.of(new JobDTO(201L, "Analyst", 40),
                        new JobDTO(202L, "ML Engineer", 40)))
        ));

        Employee entity = employeeMapper.toEntity(dto);

        Assertions.assertEquals("Alice", entity.getName());
        Assertions.assertEquals(4, entity.getJobEmployers().size()); // Each job-employer pair should be mapped separately
    }

    @Test
    void testToDTO() {
        Employee employee = new Employee(null, "Alice", new HashSet<>());

        Set<EmployeeJobEmployer> jobEmployers = getEmployeeJobEmployers(employee);

        employee.setJobEmployers(jobEmployers);

        EmployeeDTO dto = employeeMapper.toDTO(employee);

        Assertions.assertEquals("Alice", dto.getName());
        Assertions.assertEquals(2, dto.getEmployers().size()); // Should have two employers
        Assertions.assertEquals(2, dto.getEmployers().getFirst().getJobs().size()); // Should have two jobs at first
        // employer
    }

    private static Set<EmployeeJobEmployer> getEmployeeJobEmployers(Employee employee) {
        Employer employer1 = new Employer(1L, 1L, "TechCorp", new HashSet<>());
        Employer employer2 = new Employer(2L, 2L, "DataLabs", new HashSet<>());

        Job devJob = new Job(101L, 101L, "Developer", new HashSet<>());
        Job archJob = new Job(102L, 102L, "Architect", new HashSet<>());
        Job analystJob = new Job(201L, 201L, "Analyst", new HashSet<>());
        Job mlEngineerJob = new Job(202L, 202L, "ML Engineer", new HashSet<>());

        return Set.of(
                new EmployeeJobEmployer(null, employee, devJob, employer1, 0),
                new EmployeeJobEmployer(null, employee, archJob, employer1, 20),
                new EmployeeJobEmployer(null, employee, analystJob, employer2, 0),
                new EmployeeJobEmployer(null, employee, mlEngineerJob, employer2, 30)
        );
    }
}