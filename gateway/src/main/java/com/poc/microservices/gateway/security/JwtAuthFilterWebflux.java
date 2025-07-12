package com.poc.microservices.gateway.security;

import com.poc.microservices.gateway.security.helper.JwtLocalHelperGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthFilterWebflux implements WebFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilterWebflux.class);
    String keyName = "SECRET_KEY";
    String secretKey = System.getenv(keyName) != null ? System.getenv(keyName) : System.getProperty(keyName);

    @SuppressWarnings("NullableProblems")
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        logger.info("Trace ID: {}", MDC.get("traceId"));
        String requestUri = exchange.getRequest().getURI().toString();

        HttpMethod httpMethod = exchange.getRequest().getMethod();
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Bypass auth for GET /, especially for container
        //noinspection ConstantValue
        if (HttpMethod.GET.equals(httpMethod) && "/".equals(requestUri) &&
                (authHeader == null || authHeader.isEmpty())) {
            logger.debug("Skipping JWT auth for GET / and empty authentication header");
            return getDummyAdminAuth(exchange, chain);
        }

        List<String> excludedEndpoints = Arrays.asList("/gateway/test", "/api-gateway", "favicon");

        // Skip JWT validation for excluded endpoints
        if (excludedEndpoints.stream().anyMatch(requestUri::contains)) {
            logger.debug("Skipping JWT authentication for endpoint: {}", requestUri);
            return getDummyAdminAuth(exchange, chain);
        }
        String token = extractToken(exchange);
        logger.debug("Gateway received Authorization Header: {}", authHeader); // Log header before processing


        if (token == null || !validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return storeAuthContext(exchange, chain, token);
    }

    private Mono<Void> storeAuthContext(ServerWebExchange exchange, WebFilterChain chain, String token) {
        //  Store authentication context
        String role = new JwtLocalHelperGateway().getRoleFromToken(token, secretKey);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(role, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role)));

        Mono<SecurityContext> securityContext = Mono.just(new SecurityContextImpl(auth));
        exchange.getAttributes().put(SecurityContext.class.getName(), securityContext);

        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
    }

    private static Mono<Void> getDummyAdminAuth(ServerWebExchange exchange, WebFilterChain chain) {
        String admin = "ADMIN";
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(admin, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + admin)));
        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        //noinspection ConstantValue
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No valid Authorization header found!");
            return null;
        }

        return authHeader.substring(7);
    }

    private boolean validateToken(String token) {
        try {
            return new JwtLocalHelperGateway().getRoleFromToken(token, secretKey) != null;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}