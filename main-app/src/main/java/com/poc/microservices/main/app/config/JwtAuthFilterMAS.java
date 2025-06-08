package com.poc.microservices.main.app.config;

import com.poc.microservices.main.app.config.helper.JwtLocalHelperMAS;
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
import java.util.List;

public class JwtAuthFilterMAS extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilterMAS.class);

    private static final List<String> EXCLUDED_ENDPOINTS = List.of(
            "/mas/mas-users/login", "/mas/mas-users/register"
    );


    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        if (requestUri == null) return;

        // Skip JWT validation for excluded endpoints
        if (EXCLUDED_ENDPOINTS.contains(requestUri)) {
            logger.debug("Skipping JWT authentication for endpoint: {}", requestUri);
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        final String keyName = "SECRET_KEY";
        final String secretKey = System.getenv(keyName) != null ? System.getenv(keyName) : System.getProperty(keyName);


        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");
            String role = new JwtLocalHelperMAS().getRoleFromToken(token, secretKey); // Extract role

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    role,  // Authentication principal (user role)
                    token, // Credentials - the actual JWT token!
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
            logger.debug("Injecting role {} in security context", role);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }
}