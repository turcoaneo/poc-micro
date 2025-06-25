package com.poc.microservices.employee.app.repository;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.EmployeeJobEmployer;
import com.poc.microservices.employee.app.model.Employer;
import com.poc.microservices.employee.app.model.Job;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeJobEmployerRepositoryTest {

    @Autowired
    private EmployeeJobEmployerRepository employeeJobEmployerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Test
    void testReconciliationShouldNotDuplicateMappings() {
        // Step 1: Setup - as you have it
        Employee employee = new Employee();
        employee.setName("Employee X");
        employeeRepository.save(employee);

        Employer employer = new Employer(null, 12345L, "Initial Employer", new HashSet<>());
        employerRepository.save(employer);

        Job job1 = new Job(null, 101L, "Initial Job 1", new HashSet<>());
        Job job2 = new Job(null, 102L, "Initial Job 2", new HashSet<>());
        jobRepository.saveAll(Arrays.asList(job1, job2));

        EmployeeJobEmployer eje1 = new EmployeeJobEmployer(null, employee, job1, employer, 0);
        EmployeeJobEmployer eje2 = new EmployeeJobEmployer(null, employee, job2, employer, 0);
        employee.getJobEmployers().addAll(List.of(eje1, eje2));
        employer.getEmployeesJobs().addAll(List.of(eje1, eje2));
        job1.getJobEmployees().add(eje1);
        job2.getJobEmployees().add(eje2);
        employeeJobEmployerRepository.saveAll(List.of(eje1, eje2));

        employerRepository.save(employer);
        // Sanity check before update
        assertEquals(2, employee.getJobEmployers().size());
        assertEquals(2, employer.getEmployeesJobs().size());
        assertEquals(1, job1.getJobEmployees().size());
        assertEquals(1, job2.getJobEmployees().size());

        // Step 2: Fetch fresh instance and simulate name/title patch
        Employee fetched = employeeRepository.findById(employee.getEmployeeId()).orElseThrow();
        for (EmployeeJobEmployer eje : fetched.getJobEmployers()) {
            eje.getEmployer().setName("Updated Employer");
            eje.getJob().setTitle("Updated " + eje.getJob().getLocalJobId());
        }

        // Step 3: Save employee (with cascade updates)
        employeeRepository.save(fetched);

        // Step 4: Verify that employer/job names were updated, and no duplicates were introduced
        Employee reloaded = employeeRepository.findById(fetched.getEmployeeId()).orElseThrow();
        Set<EmployeeJobEmployer> updatedMappings = reloaded.getJobEmployers();

        assertEquals(2, updatedMappings.size(), "Should still have 2 mappings after update");

        for (EmployeeJobEmployer eje : updatedMappings) {
            assertEquals("Updated Employer", eje.getEmployer().getName());
            assertTrue(eje.getJob().getTitle().startsWith("Updated"), "Job title should be updated");
        }

        List<EmployeeJobEmployer> all = employeeJobEmployerRepository.findAll();
        assertEquals(2, all.size(), "No duplicate EJE entries should be created");
    }


    @Test
    void testFindEmployeesByJobAndEmployer() {
        Employer employer1 = employerRepository.save(new Employer(null, 1L, "TechCorp", new HashSet<>()));
        Employer employer2 = employerRepository.save(new Employer(null, 2L, "DataLabs", new HashSet<>()));

        Job devJob = jobRepository.save(new Job(null, 11L, "Developer", new HashSet<>()));
        Job dataJob = jobRepository.save(new Job(null, 12L, "Data Scientist", new HashSet<>()));

        Employee alice = employeeRepository.save(new Employee(null, "Alice", new HashSet<>()));
        Employee bob = employeeRepository.save(new Employee(null, "Bob", new HashSet<>()));

        employeeJobEmployerRepository.saveAll(List.of(
                new EmployeeJobEmployer(null, alice, devJob, employer1, 0),
                new EmployeeJobEmployer(null, bob, devJob, employer1, 0),
                new EmployeeJobEmployer(null, bob, dataJob, employer2, 0) // Bob works at DataLabs too
        ));

        List<Employee> employeesAtTechCorpDev = employeeJobEmployerRepository.findEmployeesByJobAndEmployer(devJob.getJobId(), employer1.getEmployerId());
        List<Employee> employeesAtDataLabsData = employeeJobEmployerRepository.findEmployeesByJobAndEmployer(dataJob.getJobId(), employer2.getEmployerId());

        assertEquals(2, employeesAtTechCorpDev.size()); // Alice & Bob work at TechCorp as Developers
        assertEquals(1, employeesAtDataLabsData.size()); // Only Bob works at DataLabs as a Data Scientist

        assertEquals("Alice", employeesAtTechCorpDev.getFirst().getName());
        assertEquals("Bob", employeesAtTechCorpDev.get(1).getName());

        assertEquals("Bob", employeesAtDataLabsData.getFirst().getName());
        assertEquals(1, employeesAtDataLabsData.size()); // Only Bob works at DataLabs
    }

    @Test
    void testFindJobsByEmployee() {
        Employer employer1 = employerRepository.save(new Employer(null, 1L, "TechCorp", new HashSet<>()));
        Employer employer2 = employerRepository.save(new Employer(null, 2L, "DataLabs", new HashSet<>()));

        Job devJob = jobRepository.save(new Job(null, 11L, "Developer", new HashSet<>()));
        Job dataJob = jobRepository.save(new Job(null, 12L, "Data Scientist", new HashSet<>()));

        Employee bob = employeeRepository.save(new Employee(null, "Bob", new HashSet<>()));

        employeeJobEmployerRepository.saveAll(List.of(
                new EmployeeJobEmployer(null, bob, devJob, employer1, 0),
                new EmployeeJobEmployer(null, bob, dataJob, employer2, 0) // Bob works at DataLabs too
        ));

        List<Job> bobJobs = employeeJobEmployerRepository.findJobsByEmployeeId(bob.getEmployeeId());

        assertEquals(2, bobJobs.size()); // Bob should have two jobs across two employers
    }
}