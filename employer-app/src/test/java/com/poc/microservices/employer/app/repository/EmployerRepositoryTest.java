package com.poc.microservices.employer.app.repository;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class EmployerRepositoryTest {

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void testFindByEmployeeIds() {
        Employee employee = new Employee();
        employee.setEmployeeId(1001L);
        employee.setName("Alice");

        Employer employer = new Employer();
        employer.setName("TechCorp");

        Job job1 = new Job();
        job1.setTitle("Developer");
        job1.setEmployer(employer);

        Job job2 = new Job();
        job2.setTitle("Architect");
        job2.setEmployer(employer);

        employee.getJobs().addAll(Set.of(job1, job2));

        employeeRepository.save(employee);

        List<Employee> result = employeeRepository.findEmployeesByEmployeeIdIn(List.of(1001L));
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Alice", result.getFirst().getName());
        //noinspection OptionalGetWithoutIsPresent
        Assertions.assertEquals("TechCorp",
                result.getFirst().getJobs().stream().findFirst().get().getEmployer().getName());
        Assertions.assertEquals(2, result.getFirst().getJobs().size());

        List<Employee> byJobsJobId = employeeRepository.findByJobsJobId(job1.getJobId());
        Assertions.assertEquals(1, byJobsJobId.size());
    }



    @Test
    void testSaveAndFindEmployer() {
        Employer employer = employerRepository.save(new Employer(null, "TechCorp", new HashSet<>()));

        Optional<Employer> retrievedEmployer = employerRepository.findById(employer.getEmployerId());
        Assertions.assertTrue(retrievedEmployer.isPresent());
        Assertions.assertEquals("TechCorp", retrievedEmployer.get().getName());
    }

    @Test
    void testDeleteEmployer() {
        Employer employer = employerRepository.save(new Employer(null, "DataLabs", new HashSet<>()));
        Long employerId = employer.getEmployerId();

        employerRepository.deleteById(employerId);
        employerRepository.flush();

        Optional<Employer> deletedEmployer = employerRepository.findById(employerId);
        Assertions.assertTrue(deletedEmployer.isEmpty());
    }
}