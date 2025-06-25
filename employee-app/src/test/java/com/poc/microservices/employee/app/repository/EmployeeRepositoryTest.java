package com.poc.microservices.employee.app.repository;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.EmployeeJobEmployer;
import com.poc.microservices.employee.app.model.Employer;
import com.poc.microservices.employee.app.model.Job;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class EmployeeRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private EmployeeJobEmployerRepository employeeJobEmployerRepository;
    @Test
    void testDeleteEmployee() {
        Entities entities = prepareDB();
        Long employeeId = entities.alice().getEmployeeId();

        entityManager.clear();

        deleteEntities(employeeId);

        // Assert removal
        Optional<Employee> removedEmployee = employeeRepository.findById(entities.alice().getEmployeeId());
        Assertions.assertTrue(removedEmployee.isEmpty()); // Employee should be deleted

        List<Job> remainingJobs = jobRepository.findAllById(List.of(entities.devJob().getJobId(), entities.dataJob().getJobId()));
        Assertions.assertTrue(remainingJobs.isEmpty()); // Jobs should be deleted

        List<Employer> remainingEmployers = employerRepository.findAllById(List.of(entities.employer1().getEmployerId(),
                entities.employer2().getEmployerId()));
        Assertions.assertTrue(remainingEmployers.isEmpty()); // Employers should be deleted
    }

    private void deleteEntities(Long employeeId) {
        // Perform deletion
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        optionalEmployee.ifPresent(employee -> {
            Set<Long> jobIds = employee.getJobEmployers().stream().map(EmployeeJobEmployer::getJob).map(Job::getJobId)
                    .collect(Collectors.toSet());
            Set<Long> employerIds = employee.getJobEmployers().stream().map(EmployeeJobEmployer::getEmployer)
                    .map(Employer::getEmployerId).collect(Collectors.toSet());
            employeeRepository.deleteById(employeeId);
            employeeRepository.flush();

            employerRepository.deleteByIds(employerIds);
            jobRepository.deleteByIds(jobIds);
        });
    }

    private Entities prepareDB() {
        // Create and persist entities
        Employer employer1 = employerRepository.save(new Employer(null, 1L,"TechCorp", new HashSet<>()));
        Employer employer2 = employerRepository.save(new Employer(null, 2L,"DataLabs", new HashSet<>()));

        Job devJob = jobRepository.save(new Job(null, 11L,"Developer", new HashSet<>()));
        Job dataJob = jobRepository.save(new Job(null, 12L,"Data Scientist", new HashSet<>()));

        Employee alice = employeeRepository.save(new Employee(null, "Alice", new HashSet<>()));

        employeeJobEmployerRepository.saveAll(List.of(
                new EmployeeJobEmployer(null, alice, devJob, employer1, 0),
                new EmployeeJobEmployer(null, alice, dataJob, employer2, 0)
        ));
        return new Entities(employer1, employer2, devJob, dataJob, alice);
    }

    private record Entities(Employer employer1, Employer employer2, Job devJob, Job dataJob, Employee alice) {
    }
}