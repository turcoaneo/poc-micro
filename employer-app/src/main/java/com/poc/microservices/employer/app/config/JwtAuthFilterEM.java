package com.poc.microservices.employer.app.config;

import com.poc.microservices.employer.app.config.helper.JwtLocalHelperEM;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JwtAuthFilterEM extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilterEM.class);


    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        if (requestUri == null) return;

        List<String> excludedEndpoints = Arrays.asList("/em/em-users/login", "/em/em-users/register", "/em/graphql");

        // Skip JWT validation for excluded endpoints
        if (excludedEndpoints.contains(requestUri)) {
            logger.debug("Skipping JWT authentication for endpoint: {}", requestUri);
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        final String keyName = "SECRET_KEY";
        final String secretKey = System.getenv(keyName) != null ? System.getenv(keyName) : System.getProperty(keyName);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");
            String role = new JwtLocalHelperEM().getRoleFromToken(token, secretKey); // Extract role

            Authentication auth = new UsernamePasswordAuthenticationToken(role, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            SecurityContextHolder.getContext().setAuthentication(auth); // Set authentication context
        }

        chain.doFilter(request, response);
    }
}