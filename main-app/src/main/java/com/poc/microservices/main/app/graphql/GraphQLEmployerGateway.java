package com.poc.microservices.main.app.graphql;

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest;
import com.netflix.graphql.dgs.client.GraphQLResponse;
import com.netflix.graphql.dgs.client.MonoGraphQLClient;
import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GraphQLEmployerGateway {

    private final MonoGraphQLClient monoGraphQLClient;

    @Autowired
    public GraphQLEmployerGateway(@Value("${em.graphql.url}") String graphqlUrl) {
        WebClient webClient = WebClient.builder()
                .baseUrl(graphqlUrl)
                .build();
        this.monoGraphQLClient = MonoGraphQLClient.createWithWebClient(webClient);
    }

    public GraphQLResponse execute(GraphQLRequest request) {
        @Language("graphql") String query = request.toQueryString();
        return monoGraphQLClient.reactiveExecuteQuery(query).block();
    }
}