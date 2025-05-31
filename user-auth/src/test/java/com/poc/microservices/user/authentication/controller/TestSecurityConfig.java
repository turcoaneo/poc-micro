package com.poc.microservices.user.authentication.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for testing
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/login", "/users/register").permitAll() // Allow all requests in tests
                        .requestMatchers("/users/**").authenticated() // Require authentication for user endpoints
                        .anyRequest().authenticated()
                );
        return httpSecurity.build();
    }
}