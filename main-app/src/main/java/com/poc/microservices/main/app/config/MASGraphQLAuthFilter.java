package com.poc.microservices.main.app.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Component
public class MASGraphQLAuthFilter implements ExchangeFilterFunction {

    @Override
    public @NotNull Mono<ClientResponse> filter(@NotNull ClientRequest request, @NotNull ExchangeFunction next) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getCredentials() != null) {
            String token = auth.getCredentials().toString();
            ClientRequest filteredRequest = ClientRequest.from(request)
                    .header("Authorization", "Bearer " + token)
                    .build();
            return next.exchange(filteredRequest);
        }
        return next.exchange(request);
    }
}