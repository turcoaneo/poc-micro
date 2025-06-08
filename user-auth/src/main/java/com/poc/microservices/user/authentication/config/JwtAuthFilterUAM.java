package com.poc.microservices.user.authentication.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.poc.microservices.user.authentication.service.helper.JwtLocalHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilterUAM extends OncePerRequestFilter {


    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        final String keyName = "SECRET_KEY";
        final String secretKey = System.getenv(keyName) != null ? System.getenv(keyName) : System.getProperty(keyName);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");
            String role = new JwtLocalHelper().getRoleFromToken(token, secretKey); // Extract role

            Authentication auth = new UsernamePasswordAuthenticationToken(role, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            SecurityContextHolder.getContext().setAuthentication(auth); // Set authentication context
        }

        chain.doFilter(request, response);
    }
}