package com.poc.microservices.employer.app.controller;

import com.poc.microservices.employer.app.config.GraphQLScalarConfig;
import com.poc.microservices.employer.app.graphql.GraphQLEmployeeRecord;
import com.poc.microservices.employer.app.graphql.GraphQLEmployerRecord;
import com.poc.microservices.employer.app.graphql.GraphQLJobRecord;
import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.GraphQLWorkingHoursContext;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursRequestDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursResponseDTO;
import com.poc.microservices.employer.app.repository.EmployeeRepository;
import com.poc.microservices.employer.app.repository.EmployerRepository;
import com.poc.microservices.employer.app.service.EMWorkingHoursService;
import com.poc.microservices.employer.app.service.util.GraphQLEmployerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@GraphQlTest({GraphqlEmployerQueryResolver.class, GraphqlJobFieldResolver.class})
@Import({GraphQLScalarConfig.class})
class GraphqlJobFieldResolverTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @MockitoBean
    private EmployerRepository employerRepository;

    @MockitoBean
    private GraphQLEmployerMapper mapper;

    @MockitoBean
    EMWorkingHoursService emWorkingHoursService;

    @MockitoBean
    private GraphQLWorkingHoursContext mockContext;

    @BeforeEach
    public void setUp() {
        Mockito.when(mockContext.getEmployerId()).thenReturn(null);
    }

    @Test
        //@Disabled // while disabling  @SchemaMapping
    void testResolveEmployeesOnJob() {
        // Arrange domain model
        Employer employer = new Employer(1L, "Employer 1", new HashSet<>());
        Job job = new Job(101L, "Job 1", employer, null, null, new HashSet<>());
        employer.getJobs().add(job);

        Employee emp1 = new Employee(1L, 1L, "Employee 1", true, Set.of(job));
        Employee emp2 = new Employee(2L, 1L, "Employee 2", true, Set.of(job));

        // Set up GraphQL-level output with null employees (so @SchemaMapping is triggered)
        GraphQLJobRecord gqlJob = new GraphQLJobRecord(101L, "Job 1", null);
        GraphQLEmployerRecord gqlEmployer = new GraphQLEmployerRecord(1L, "Employer 1", List.of(gqlJob));

        GraphQLEmployeeRecord gqlEmp1 = new GraphQLEmployeeRecord(1L, "Alice", null);
        GraphQLEmployeeRecord gqlEmp2 = new GraphQLEmployeeRecord(2L, "Bob", null);

        // Mock mappings and repositories
        Mockito.when(employerRepository.findAll()).thenReturn(List.of(employer));
        Mockito.when(mapper.toGraphQLRecord(employer)).thenReturn(gqlEmployer);
        Mockito.when(mapper.toGraphQLRecord(job)).thenReturn(gqlJob);
        Mockito.when(employeeRepository.findByJobsJobId(101L)).thenReturn(List.of(emp1, emp2));
        Mockito.when(mapper.toGraphQLRecord(emp1)).thenReturn(gqlEmp1);
        Mockito.when(mapper.toGraphQLRecord(emp2)).thenReturn(gqlEmp2);
        Mockito.when(emWorkingHoursService.getWorkingHours(Mockito.any(EMWorkingHoursRequestDTO.class)))
                .thenReturn(new EMWorkingHoursResponseDTO());

        // Act + Assert
        graphQlTester.document("""
                        {
                          employers {
                            name
                            jobs {
                              title
                              employees {
                                employeeId
                                name
                              }
                            }
                          }
                        }
                        """)
                .execute()
                .path("employers[0].jobs[0].employees")
                .entityList(GraphQLEmployeeRecord.class)
                .hasSize(2)
                .contains(gqlEmp1, gqlEmp2);
    }
}