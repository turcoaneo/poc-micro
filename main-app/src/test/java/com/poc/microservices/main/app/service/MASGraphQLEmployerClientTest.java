package com.poc.microservices.main.app.service;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import com.netflix.graphql.dgs.client.GraphQLResponse;
import com.poc.microservice.main.app.generated.graphql.Employer;
import com.poc.microservices.main.app.graphql.MASGraphQLEmployerClient;
import com.poc.microservices.main.app.graphql.MASGraphQLEmployerGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class MASGraphQLEmployerClientTest {

    @Mock
    private MASGraphQLEmployerGateway gateway;

    @InjectMocks
    private MASGraphQLEmployerClient MASGraphQLEmployerClient;

    @Test
    void shouldReturnEmployerWhenResponseIsValid() {
        // given
        Employer expected = new Employer();
        expected.setEmployerId(42L);
        expected.setName("Acme Corp");

        GraphQLResponse mockResponse = Mockito.mock(GraphQLResponse.class);
        Mockito.when(mockResponse.extractValueAsObject(ArgumentMatchers.eq("employer"),
                        ArgumentMatchers.eq(Employer.class)))
                .thenReturn(expected);

        Mockito.when(gateway.execute(Mockito.any(GraphQLRequest.class)))
                .thenReturn(mockResponse);

        // when
        Employer actual = MASGraphQLEmployerClient.fetchEmployerById(42L);

        // then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals("Acme Corp", actual.getName());
    }

    @Test
    void shouldReturnNullWhenResponseIsNull() {
        // given
        Mockito.when(gateway.execute(Mockito.any(GraphQLRequest.class))).thenReturn(null);

        // when
        Employer actual = MASGraphQLEmployerClient.fetchEmployerById(42L);

        // then
        Assertions.assertNull(actual);
    }
}