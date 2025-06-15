package com.poc.microservices.employer.app.repository;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class EmployeeRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Long localEmployeeId;

    @BeforeEach
    public void setUp() {
        Employer employer = new Employer(null, "employer", new HashSet<>());
        Set<Job> jobs = employer.getJobs();
        Job job1 = new Job(null, "job 1", employer, "desc 1", 10D, new HashSet<>());
        Job job2 = new Job(null, "job 2", employer, "desc 2", 20D, new HashSet<>());
        jobs.addAll(Arrays.asList(job1, job2));
        testEntityManager.persist(employer);


        Employee alice = new Employee(null, 33L, "Alice", true, new HashSet<>());
        alice.getJobs().add(job1);
        job1.getEmployees().add(alice);
        Employee employee = employeeRepository.save(alice);
        localEmployeeId = employee.getLocalEmployeeId();
        testEntityManager.clear();
    }

    @Test
    void testSaveAndFindEmployee() {
        Optional<Employee> retrievedEmployee = employeeRepository.findById(localEmployeeId);
        Assertions.assertTrue(retrievedEmployee.isPresent());
        Assertions.assertEquals("Alice", retrievedEmployee.get().getName());
    }

    @Test
    void testDeleteEmployee() {
        employeeRepository.deleteById(localEmployeeId);
        employeeRepository.flush();

        Optional<Employee> deletedEmployee = employeeRepository.findById(localEmployeeId);
        Assertions.assertTrue(deletedEmployee.isEmpty());
    }
}