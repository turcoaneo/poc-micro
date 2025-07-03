package com.poc.microservices.gateway.config;

import com.poc.microservices.gateway.security.JwtAuthFilterWebflux;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Configuration
@EnableWebFluxSecurity
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("mas-service", r -> r.path("/mas/**")
                        .uri("lb://mas-service"))
                .route("uam-service", r -> r.path("/uam/**")
                        .uri("lb://uam-service"))
                .route("my_route", r -> r.path("/gateway/test/**")
                        .uri("http://localhost:8090/test_service"))
                .route("em-service", r -> r.path("/em/**")
                        .uri("lb://em-service"))
                .route("eem-service", r -> r.path("/eem/**")
                        .uri("lb://eem-service"))
                .build();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated() // Debugging step—allow all traffic for testing
                )
                .addFilterAt(new JwtAuthFilterWebflux(), SecurityWebFiltersOrder.AUTHORIZATION) // Apply JWT before authorization
//                .addFilterBefore(new JwtAuthFilter(), SecurityWebFiltersOrder.AUTHORIZATION)
                .build();
    }

    @Bean
    public ServerSecurityContextRepository securityContextRepository() {
        return new ServerSecurityContextRepository() {
            @Override
            public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
                exchange.getAttributes().put(SecurityContext.class.getName(), Mono.just(context));
                return Mono.empty();
            }

            @Override
            public Mono<SecurityContext> load(ServerWebExchange exchange) {
                return exchange.getAttributeOrDefault(SecurityContext.class.getName(), Mono.empty());
            }
        };
    }
}