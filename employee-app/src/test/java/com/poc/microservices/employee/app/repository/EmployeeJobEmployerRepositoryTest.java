package com.poc.microservices.employee.app.repository;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.EmployeeJobEmployer;
import com.poc.microservices.employee.app.model.Employer;
import com.poc.microservices.employee.app.model.Job;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testFindEmployeesByJobAndEmployer() {
        Employer employer1 = employerRepository.save(new Employer(null, "TechCorp", new HashSet<>()));
        Employer employer2 = employerRepository.save(new Employer(null, "DataLabs", new HashSet<>()));

        Job devJob = jobRepository.save(new Job(null, "Developer", new HashSet<>()));
        Job dataJob = jobRepository.save(new Job(null, "Data Scientist", new HashSet<>()));

        Employee alice = employeeRepository.save(new Employee(null, "Alice", 40, new HashSet<>()));
        Employee bob = employeeRepository.save(new Employee(null, "Bob", 35, new HashSet<>()));

        employeeJobEmployerRepository.saveAll(List.of(
                new EmployeeJobEmployer(null, alice, devJob, employer1),
                new EmployeeJobEmployer(null, bob, devJob, employer1),
                new EmployeeJobEmployer(null, bob, dataJob, employer2) // Bob works at DataLabs too
        ));

        List<Employee> employeesAtTechCorpDev = employeeJobEmployerRepository.findEmployeesByJobAndEmployer(devJob.getId(), employer1.getId());
        List<Employee> employeesAtDataLabsData = employeeJobEmployerRepository.findEmployeesByJobAndEmployer(dataJob.getId(), employer2.getId());

        assertEquals(2, employeesAtTechCorpDev.size()); // Alice & Bob work at TechCorp as Developers
        assertEquals(1, employeesAtDataLabsData.size()); // Only Bob works at DataLabs as a Data Scientist
    }

    @Test
    void testFindJobsByEmployee() {
        Employer employer1 = employerRepository.save(new Employer(null, "TechCorp", new HashSet<>()));
        Employer employer2 = employerRepository.save(new Employer(null, "DataLabs", new HashSet<>()));

        Job devJob = jobRepository.save(new Job(null, "Developer", new HashSet<>()));
        Job dataJob = jobRepository.save(new Job(null, "Data Scientist", new HashSet<>()));

        Employee bob = employeeRepository.save(new Employee(null, "Bob", 35, new HashSet<>()));

        employeeJobEmployerRepository.saveAll(List.of(
                new EmployeeJobEmployer(null, bob, devJob, employer1),
                new EmployeeJobEmployer(null, bob, dataJob, employer2) // Bob works at DataLabs too
        ));

        List<Job> bobJobs = employeeJobEmployerRepository.findJobsByEmployeeId(bob.getId());

        assertEquals(2, bobJobs.size()); // Bob should have two jobs across two employers
    }
}