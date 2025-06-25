package com.poc.microservices.main.app.graphql;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import com.netflix.graphql.dgs.client.GraphQLResponse;
import com.poc.microservice.main.app.generated.graphql.EmployeeResponseProjection;
import com.poc.microservice.main.app.generated.graphql.Employer;
import com.poc.microservice.main.app.generated.graphql.EmployerQueryRequest;
import com.poc.microservice.main.app.generated.graphql.EmployerResponseProjection;
import com.poc.microservice.main.app.generated.graphql.EmployersQueryRequest;
import com.poc.microservice.main.app.generated.graphql.JobResponseProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GraphQLEmployerClient {

    private final GraphQLEmployerGateway graphQLEmployerGateway;

    @Autowired
    public GraphQLEmployerClient(GraphQLEmployerGateway graphQLEmployerGateway) {
        this.graphQLEmployerGateway = graphQLEmployerGateway;
    }

    public Employer fetchEmployerById(Long id, Long employeeId) {
        EmployerQueryRequest request = EmployerQueryRequest.builder().setId(id).setEmployeeId(employeeId).build();

        EmployerResponseProjection projection = new EmployerResponseProjection()
                .employerId()
                .name()
                .jobs(
                        new JobResponseProjection()
                                .jobId()
                                .title()
                                .employees(
                                        new EmployeeResponseProjection()
                                                .employeeId()
                                                .name()
                                                .hours()
                                )
                );

        GraphQLRequest gqlRequest = new GraphQLRequest(request, projection);
        GraphQLResponse response = graphQLEmployerGateway.execute(gqlRequest);

        if (response != null) {
            return response.extractValueAsObject("employer", Employer.class);
        } else {
            return null;
        }
    }

    public List<Employer> fetchEmployers() {
        EmployersQueryRequest request = EmployersQueryRequest.builder().build();

        EmployerResponseProjection projection = new EmployerResponseProjection()
                .employerId()
                .name()
                .jobs(
                        new JobResponseProjection()
                                .jobId()
                                .title()
                                .employees(
                                        new EmployeeResponseProjection()
                                                .employeeId()
                                                .name()
                                )
                );

        GraphQLRequest gqlRequest = new GraphQLRequest(request, projection);
        GraphQLResponse response = graphQLEmployerGateway.execute(gqlRequest);

        if (response != null) {
            Object raw = response.getData().get("employers");
            return new ObjectMapper().convertValue(raw, new TypeReference<>() {});
        } else {
            return null;
        }
    }
}