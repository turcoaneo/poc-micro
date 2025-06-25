package com.poc.microservices.employee.app.repository;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.EmployeeJobEmployer;
import com.poc.microservices.employee.app.model.Employer;
import com.poc.microservices.employee.app.model.Job;
import com.poc.microservices.employee.app.model.dto.JobWorkingHoursDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeJobEmployerWorkingHoursRepositoryTest {

    @Autowired
    private EmployeeJobEmployerRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    void shouldReturnWorkingHoursForEmployeeAndEmployer() {
        Employee emp = new Employee();
        emp.setName("Ada");
        Employer org = new Employer();
        org.setEmployerId(10L);
        org.setName("MAS");
        Job dev = new Job();
        dev.setTitle("Dev");
        dev.setJobId(100L);
        em.persist(emp);
        em.persist(org);
        em.persist(dev);

        EmployeeJobEmployer eje = new EmployeeJobEmployer();
        eje.setEmployee(emp);
        emp.getJobEmployers().add(eje);
        eje.setEmployer(org);
        org.getEmployeesJobs().add(eje);
        eje.setJob(dev);
        dev.getJobEmployees().add(eje);
        eje.setWorkingHours(36);
        em.persist(eje);
        em.flush();

        List<JobWorkingHoursDTO> result = repository.findWorkingHoursByEmployeeEmployerAndJobs(org.getEmployerId(),
                List.of(100L));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(dev.getJobId(), result.getFirst().getJobId());
        Assertions.assertEquals(dev.getJobId(), result.getFirst().getJobId());
        Assertions.assertEquals(emp.getEmployeeId(), result.getFirst().getEmployeeId());
        Assertions.assertEquals(36, result.getFirst().getWorkingHours());

        result = repository.findWorkingHoursByEmployeeEmployerAndJobs(org.getEmployerId(), List.of(100L));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(dev.getJobId(), result.getFirst().getJobId());
        Assertions.assertEquals(36, result.getFirst().getWorkingHours());

        result = repository.findWorkingHoursByEmployeeEmployerAndJobs(org.getLocalEmployerId(), List.of(100L));
        Assertions.assertEquals(0, result.size());
    }
}