package com.poc.microservices.employer.app.config;

import com.poc.microservices.employer.app.config.helper.JwtLocalHelperEM;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class GraphQLSecurityContextInterceptor implements WebGraphQlInterceptor {

    @SuppressWarnings("NullableProblems")
    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        final String keyName = "SECRET_KEY";
        final String secretKey = System.getenv(keyName) != null ? System.getenv(keyName) : System.getProperty(keyName);


        //noinspection ConstantValue
        if (authHeader!= null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = new JwtLocalHelperEM().getRoleFromToken(token, secretKey);

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    role,
                    token,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        return chain.next(request);
    }
}