package com.poc.microservices.employer.app.repository;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use real DB
class EmployerRepositoryTest {

    @Autowired
    private EmployerRepository employerRepository;

    @Test
    void testCreateEmployer_NoJobsNoEmployees() {
        Employer employer = new Employer();
        employer.setName("TechCorp");

        Employer savedEmployer = employerRepository.save(employer);

        assertNotNull(savedEmployer.getId());
        assertEquals("TechCorp", savedEmployer.getName());
        assertTrue(savedEmployer.getJobs().isEmpty());
        assertTrue(savedEmployer.getEmployees().isEmpty());
    }

    @Test
    void testCreateEmployerWithJobs() {
        Employer employer = new Employer();
        employer.setName("TechCorp");

        Job job1 = new Job();
        job1.setTitle("Software Engineer");
        job1.setDescription("Develop microservices");
        job1.setHourRate(50.0);
        job1.setEmployer(employer);

        Job job2 = new Job();
        job2.setTitle("Data Scientist");
        job2.setDescription("Analyze large datasets");
        job2.setHourRate(55.0);
        job2.setEmployer(employer);

        employer.getJobs().add(job1);
        employer.getJobs().add(job2);

        Employer savedEmployer = employerRepository.save(employer);

        assertEquals(2, savedEmployer.getJobs().size());
        assertTrue(savedEmployer.getJobs().stream().anyMatch(j -> j.getTitle().equals("Software Engineer")));
    }

    @Test
    void testAssignEmployeesToJobs() {
        Employer employer = new Employer();
        employer.setName("TechCorp");

        Job job1 = new Job();
        job1.setTitle("Software Engineer");
        job1.setEmployer(employer);

        Job job2 = new Job();
        job2.setTitle("Data Scientist");
        job2.setEmployer(employer);

        Employee employee1 = new Employee();
        employee1.setName("Alice");
        employee1.setEmployer(employer);
        employee1.getAssignedJobs().add(job1);

        Employee employee2 = new Employee();
        employee2.setName("Bob");
        employee2.setEmployer(employer);
        employee2.getAssignedJobs().add(job2);
        employee2.getAssignedJobs().add(job1); // Bob works on two jobs

        job1.getAssignedEmployees().add(employee1);
        job1.getAssignedEmployees().add(employee2);
        job2.getAssignedEmployees().add(employee2);

        employer.getEmployees().add(employee1);
        employer.getEmployees().add(employee2);

        Employer savedEmployer = employerRepository.save(employer);

        assertEquals(2, savedEmployer.getEmployees().size());
        assertEquals(2, job1.getAssignedEmployees().size()); // Software Engineer job has two employees
        assertEquals(1, job2.getAssignedEmployees().size()); // Data Scientist job has one employee
    }
}