package com.poc.microservices.main.app.graphql;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import com.poc.microservice.main.app.generated.graphql.EmployeeResponseProjection;
import com.poc.microservice.main.app.generated.graphql.Employer;
import com.poc.microservice.main.app.generated.graphql.EmployerQueryRequest;
import com.poc.microservice.main.app.generated.graphql.EmployerResponseProjection;
import com.poc.microservice.main.app.generated.graphql.EmployersQueryRequest;
import com.poc.microservice.main.app.generated.graphql.JobResponseProjection;
import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ContextSnapshot;
import io.micrometer.context.ContextSnapshotFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@Service
public class MASGraphQLEmployerClient {

    private final MASHttpGraphQLEmployerGateway gateway;

    @Autowired
    public MASGraphQLEmployerClient(MASHttpGraphQLEmployerGateway gateway) {
        this.gateway = gateway;
    }

    public Employer fetchEmployerById(Long id) {
        GraphQLRequest gqlRequest = new GraphQLRequest(
                EmployerQueryRequest.builder().setId(id).build(),
                buildEmployerWithHoursProjection()
        );

        return executeWithTrace(() -> {
            ClientGraphQlResponse response = gateway.execute(gqlRequest).block();
            if (response != null && response.isValid()) {
                return response.field("employer").toEntity(Employer.class);
            }
            return null;
        });
    }

    public List<Employer> fetchEmployers() {
        GraphQLRequest gqlRequest = new GraphQLRequest(
                EmployersQueryRequest.builder().build(),
                buildBaseEmployerProjection()
        );

        return executeWithTrace(() -> {
            ClientGraphQlResponse response = gateway.execute(gqlRequest).block();
            if (response != null && response.isValid()) {
                return response.field("employers").toEntityList(Employer.class);
            }
            return Collections.emptyList();
        });
    }

    private EmployerResponseProjection buildEmployerWithHoursProjection() {
        EmployeeResponseProjection employeeProjection = getEmployeeResponseProjection();
        employeeProjection.hours();
        return getEmployerResponseProjection(employeeProjection);
    }

    private EmployerResponseProjection buildBaseEmployerProjection() {
        EmployeeResponseProjection employeeProjection = getEmployeeResponseProjection();
        return getEmployerResponseProjection(employeeProjection);
    }

    private static EmployeeResponseProjection getEmployeeResponseProjection() {
        return new EmployeeResponseProjection()
                .employeeId()
                .name();
    }

    private static EmployerResponseProjection getEmployerResponseProjection(EmployeeResponseProjection employeeProjection) {
        return new EmployerResponseProjection()
                .employerId()
                .name()
                .jobs(
                        new JobResponseProjection()
                                .jobId()
                                .title()
                                .employees(employeeProjection)
                );
    }

    private <T> T executeWithTrace(Callable<T> callable) {
        ContextSnapshot snapshot = ContextSnapshotFactory.builder()
                .contextRegistry(ContextRegistry.getInstance())
                .build()
                .captureAll();

        try {
            return snapshot.wrap(callable).call();
        } catch (Exception e) {
            throw new RuntimeException("GraphQL execution failed", e);
        }
    }
}