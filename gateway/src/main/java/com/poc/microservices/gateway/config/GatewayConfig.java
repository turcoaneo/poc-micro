package com.poc.microservices.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("mas-service", r -> r.path("/mas/**")
                        .uri("lb://mas-service"))
                .route("uam-service", r -> r.path("/uam/**")
                        .uri("lb://uam-service"))
                .build();
    }
}