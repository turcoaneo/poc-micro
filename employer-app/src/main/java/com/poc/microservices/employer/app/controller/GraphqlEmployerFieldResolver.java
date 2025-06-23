package com.poc.microservices.employer.app.controller;

import com.poc.microservices.employer.app.graphql.GraphQLEmployerRecord;
import com.poc.microservices.employer.app.graphql.GraphQLJobRecord;
import com.poc.microservices.employer.app.repository.JobRepository;
import com.poc.microservices.employer.app.service.GraphQLEmployerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GraphqlEmployerFieldResolver {

    private final JobRepository jobRepository;
    private final GraphQLEmployerMapper mapper;

    @SchemaMapping(typeName = "Employer", field = "jobs")
    public List<GraphQLJobRecord> resolveJobs(GraphQLEmployerRecord employer) {
        return jobRepository.findByEmployerEmployerId(employer.employerId()).stream()
            .map(mapper::toGraphQLRecord)
            .toList();
    }
}