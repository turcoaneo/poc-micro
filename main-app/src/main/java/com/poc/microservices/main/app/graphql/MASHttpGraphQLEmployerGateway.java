package com.poc.microservices.main.app.graphql;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import com.poc.microservices.main.app.config.MASGraphQLAuthFilter;
import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class MASHttpGraphQLEmployerGateway {

    private final HttpGraphQlClient graphQlClient;

    @Autowired
    public MASHttpGraphQLEmployerGateway(
            @Value("${em.graphql.service}") String graphqlUrl,
            MASGraphQLAuthFilter authFilter,
            @Qualifier("myWebClientBuilder") WebClient.Builder webClientBuilder) {

        WebClient webClient = webClientBuilder
                .baseUrl(graphqlUrl)
                .filter(authFilter) // JWT stays in place
                .build();

        this.graphQlClient = HttpGraphQlClient.builder(webClient).build();
    }

    public Mono<ClientGraphQlResponse> execute(GraphQLRequest request) {
        @Language("graphql") String query = request.toQueryString();
        return graphQlClient.document(query).execute();
    }
}