package com.poc.microservices.main.app.graphql;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import com.netflix.graphql.dgs.client.GraphQLResponse;
import com.netflix.graphql.dgs.client.MonoGraphQLClient;
import com.poc.microservices.main.app.config.MASGraphQLAuthFilter;
import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MASGraphQLEmployerGateway {

    private final MonoGraphQLClient monoGraphQLClient;

    @Autowired
    public MASGraphQLEmployerGateway(@Value("${em.graphql.url}") String graphqlUrl, MASGraphQLAuthFilter authFilter) {
        WebClient webClient = WebClient.builder()
                .baseUrl(graphqlUrl)
                .filter(authFilter) // JWT here
                .build();

        this.monoGraphQLClient = MonoGraphQLClient.createWithWebClient(webClient);
    }

    public GraphQLResponse execute(GraphQLRequest request) {
        @Language("graphql") String query = request.toQueryString();
        return monoGraphQLClient.reactiveExecuteQuery(query).block();
    }
}