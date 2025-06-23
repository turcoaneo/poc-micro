package com.poc.microservices.employer.app.controller;

import com.poc.microservices.employer.app.config.GraphQLScalarConfig;
import com.poc.microservices.employer.app.graphql.GraphQLEmployerRecord;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.repository.EmployerRepository;
import com.poc.microservices.employer.app.service.GraphQLEmployerMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@GraphQlTest(GraphqlEmployerQueryResolver.class)
@Import(GraphQLScalarConfig.class)
class GraphqlEmployerQueryResolverTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private EmployerRepository employerRepository;

    @MockitoBean
    private GraphQLEmployerMapper employerMapper;

    @Test
    void testEmployersQuery() {
        Employer employer = new Employer(1L, "TechCorp", new HashSet<>());
        GraphQLEmployerRecord gqlRecord = new GraphQLEmployerRecord(1L, "TechCorp", List.of());

        Mockito.when(employerRepository.findAll()).thenReturn(List.of(employer));
        Mockito.when(employerMapper.toGraphQLRecord(employer)).thenReturn(gqlRecord);

        graphQlTester.document("{ employers { employerId name } }")
            .execute()
            .path("employers[0].name")
            .entity(String.class)
            .isEqualTo("TechCorp");
    }

    @Test
    void testEmployerByIdQuery() {
        Employer employer = new Employer(42L, "DataLabs", new HashSet<>());
        GraphQLEmployerRecord gqlRecord = new GraphQLEmployerRecord(42L, "DataLabs", List.of());

        Mockito.when(employerRepository.findById(42L)).thenReturn(Optional.of(employer));
        Mockito.when(employerMapper.toGraphQLRecord(employer)).thenReturn(gqlRecord);

        graphQlTester.document("query($id: Long!) { employer(id: $id) { name } }")
            .variable("id", 42L)
            .execute()
            .path("employer.name")
            .entity(String.class)
            .isEqualTo("DataLabs");
    }
}