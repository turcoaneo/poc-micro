package com.poc.microservices.main.app.service;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import com.poc.microservice.main.app.generated.graphql.Employer;
import com.poc.microservices.main.app.graphql.MASGraphQLEmployerClient;
import com.poc.microservices.main.app.graphql.MASHttpGraphQLEmployerGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.ClientResponseField;
import reactor.core.publisher.Mono;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class MASGraphQLEmployerClientTest {

    @Mock
    private MASHttpGraphQLEmployerGateway gateway;

    @InjectMocks
    private MASGraphQLEmployerClient MASGraphQLEmployerClient;

    @Test
    void shouldReturnEmployerWhenResponseIsValid() {
        // given
        Employer expected = new Employer();
        expected.setEmployerId(42L);
        expected.setName("Acme Corp");

        ClientGraphQlResponse mockResponse = Mockito.mock(ClientGraphQlResponse.class);
        ClientResponseField mockField = Mockito.mock(ClientResponseField.class);

        Mockito.when(mockField.toEntity(Employer.class)).thenReturn(expected);
        Mockito.when(mockResponse.isValid()).thenReturn(true);
        Mockito.when(mockResponse.field("employer")).thenReturn(mockField);
        Mockito.when(gateway.execute(Mockito.any(GraphQLRequest.class)))
                .thenReturn(Mono.just(mockResponse));

        // when
        Employer actual = MASGraphQLEmployerClient.fetchEmployerById(42L);

        // then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals("Acme Corp", actual.getName());
    }

    @Test
    void shouldReturnNullWhenResponseIsNull() {
        // given
        Mockito.when(gateway.execute(Mockito.any(GraphQLRequest.class)))
                .thenReturn(Mono.justOrEmpty(Optional.empty())); // Mimics gateway returning no response

        // when
        Employer actual = MASGraphQLEmployerClient.fetchEmployerById(42L);

        // then
        Assertions.assertNull(actual);
    }
}