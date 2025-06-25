package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.graphql.GraphQLEmployeeRecord;
import com.poc.microservices.employer.app.graphql.GraphQLEmployerRecord;
import com.poc.microservices.employer.app.graphql.GraphQLJobRecord;
import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.service.util.GraphQLEmployerMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class GraphQLEmployerMapperTest {

    @Spy
    private GraphQLEmployerMapper mapper;

    @Test
    void testToGraphQLRecord() {
        // Create test employee
        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("Alice");

        // Create test job
        Job job = new Job();
        job.setJobId(101L);
        job.setTitle("Developer");
        job.setEmployees(Set.of(employee));

        // Create test employer
        Employer employer = new Employer();
        employer.setEmployerId(10L);
        employer.setName("TechCorp");
        employer.setJobs(Set.of(job));

        // Map to GraphQL record
        GraphQLEmployerRecord gqlEmployer = mapper.toGraphQLRecord(employer);

        Assertions.assertEquals(10L, gqlEmployer.employerId());
        Assertions.assertEquals("TechCorp", gqlEmployer.name());
        Assertions.assertEquals(1, gqlEmployer.jobs().size());

        GraphQLJobRecord gqlJob = gqlEmployer.jobs().getFirst();
        Assertions.assertEquals(101L, gqlJob.jobId());
        Assertions.assertEquals("Developer", gqlJob.title());
        Assertions.assertEquals(1, gqlJob.employees().size());

        GraphQLEmployeeRecord gqlEmployee = gqlJob.employees().getFirst();
        Assertions.assertEquals(1L, gqlEmployee.employeeId());
        Assertions.assertEquals("Alice", gqlEmployee.name());
    }
}