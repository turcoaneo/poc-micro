package com.poc.microservices.employer.app.controller;

import com.poc.microservices.employer.app.graphql.GraphQLEmployeeRecord;
import com.poc.microservices.employer.app.graphql.GraphQLJobRecord;
import com.poc.microservices.employer.app.repository.EmployeeRepository;
import com.poc.microservices.employer.app.service.GraphQLEmployerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GraphqlJobFieldResolver {

    private final EmployeeRepository employeeRepository;
    private final GraphQLEmployerMapper mapper;

    @SchemaMapping(typeName = "Job", field = "employees")
    public List<GraphQLEmployeeRecord> resolveEmployees(GraphQLJobRecord job) {
        return employeeRepository.findByJobsJobId(job.jobId()).stream()
            .map(mapper::toGraphQLRecord)
            .toList();
    }
}