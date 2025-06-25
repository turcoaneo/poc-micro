package com.poc.microservices.employer.app.controller;

import com.poc.microservices.employer.app.config.GraphQLScalarConfig;
import com.poc.microservices.employer.app.graphql.GraphQLEmployerRecord;
import com.poc.microservices.employer.app.graphql.GraphQLJobRecord;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.GraphQLWorkingHoursContext;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.repository.EmployerRepository;
import com.poc.microservices.employer.app.repository.JobRepository;
import com.poc.microservices.employer.app.service.util.GraphQLEmployerMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashSet;
import java.util.List;

@GraphQlTest({GraphqlEmployerQueryResolver.class, GraphqlEmployerFieldResolver.class})
@Import({GraphQLScalarConfig.class, GraphQLEmployerMapper.class})
class GraphqlEmployerFieldResolverTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private EmployerRepository employerRepository;

    @MockitoBean
    private JobRepository jobRepository;

    @MockitoBean
    private GraphQLEmployerMapper mapper;

    @MockitoBean
    private GraphQLWorkingHoursContext mockContext;

    @Test
    void testEmployerJobsField() {
        // Domain setup
        Employer employer = new Employer(1L, "Employer 1", new HashSet<>());

        Job job1 = new Job(101L, "Job 1", employer, null, null, new HashSet<>());
        Job job2 = new Job(102L, "Job 2", employer, null, null, new HashSet<>());

        employer.getJobs().addAll(List.of(job1, job2));

        // GraphQL output
        GraphQLEmployerRecord gqlEmployer = new GraphQLEmployerRecord(1L, "Employer 1", null); // crucial: jobs = null
        GraphQLJobRecord gqlJob1 = new GraphQLJobRecord(101L, "Job 1", List.of());
        GraphQLJobRecord gqlJob2 = new GraphQLJobRecord(102L, "Job 2", List.of());

        // Mocks
        Mockito.when(employerRepository.findAll()).thenReturn(List.of(employer));
        Mockito.when(jobRepository.findByEmployerEmployerId(1L)).thenReturn(List.of(job1, job2));

        Mockito.when(mapper.toGraphQLRecord(employer)).thenReturn(gqlEmployer);
        Mockito.when(mapper.toGraphQLRecord(job1)).thenReturn(gqlJob1);
        Mockito.when(mapper.toGraphQLRecord(job2)).thenReturn(gqlJob2);

        Mockito.when(mockContext.getEmployeeId()).thenReturn(null);
        Mockito.when(mockContext.getEmployerId()).thenReturn(null);

        // Query execution
        graphQlTester.document("""
            {
              employers {
                name
                jobs {
                  title
                }
              }
            }
            """)
                .execute()
                .path("employers[0].jobs")
                .entityList(GraphQLJobRecord.class)
                .hasSize(2);
    }
}