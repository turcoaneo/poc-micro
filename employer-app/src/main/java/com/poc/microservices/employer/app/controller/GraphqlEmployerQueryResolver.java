package com.poc.microservices.employer.app.controller;

import com.poc.microservices.employer.app.graphql.GraphQLEmployerRecord;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.GraphQLWorkingHoursContext;
import com.poc.microservices.employer.app.repository.EmployerRepository;
import com.poc.microservices.employer.app.service.util.GraphQLEmployerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GraphqlEmployerQueryResolver {

    private final EmployerRepository employerRepository;
    private final GraphQLEmployerMapper employerMapper;
    private final GraphQLWorkingHoursContext workingHoursContext;

    @QueryMapping
    public GraphQLEmployerRecord employer(@Argument Long id) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        workingHoursContext.setEmployerId(id);

        return employerMapper.toGraphQLRecord(employer);
    }

    @QueryMapping
    public List<GraphQLEmployerRecord> employers() {
        return employerRepository.findAll().stream()
                .map(employerMapper::toGraphQLRecord)
                .toList();
    }
}