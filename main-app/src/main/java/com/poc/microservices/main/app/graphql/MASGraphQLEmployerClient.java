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

@Service
public class MASGraphQLEmployerClient {

    private final MASHttpGraphQLEmployerGateway masHttpGraphQLEmployerGateway;

    @Autowired
    public MASGraphQLEmployerClient(MASHttpGraphQLEmployerGateway masHttpGraphQLEmployerGateway) {
        this.masHttpGraphQLEmployerGateway = masHttpGraphQLEmployerGateway;
    }

    public Employer fetchEmployerById(Long id) {
        EmployerQueryRequest request = EmployerQueryRequest.builder().setId(id).build();

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
        // Capture the current context
        ContextSnapshot snapshot = ContextSnapshotFactory.builder()
                .contextRegistry(ContextRegistry.getInstance())
                .build()
                .captureAll();

        try {
            return snapshot
                    .wrap(() -> {
                        ClientGraphQlResponse response = masHttpGraphQLEmployerGateway.execute(gqlRequest).block();
                        if (response != null && response.isValid()) {
                            return response.field("employer").toEntity(Employer.class);
                        }
                        return null;
                    })
                    .call();
        } catch (Exception e) {
            throw new RuntimeException("GraphQL execution failed", e);
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
        ContextSnapshot snapshot = ContextSnapshotFactory.builder()
                .contextRegistry(ContextRegistry.getInstance())
                .build()
                .captureAll();
        try {
            //noinspection unchecked
            return (List<Employer>)snapshot.wrap(() -> {
                ClientGraphQlResponse response = masHttpGraphQLEmployerGateway.execute(gqlRequest).block();
                if (response != null && response.isValid()) {
                    return response.field("employers").toEntityList(Employer.class);
                }
                return Collections.emptyList();
            }).call();
        } catch (Exception e) {
            throw new RuntimeException("GraphQL execution failed", e);
        }
    }
}