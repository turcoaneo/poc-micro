package com.poc.microservices.employer.app.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class EMFeignAuthInterceptor implements RequestInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(EMFeignAuthInterceptor.class);

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getCredentials() != null) {
            String token = authentication.getCredentials().toString();
            logger.info("Feign EM request attaching Authorization: Bearer {}", token); // Log token before attaching
            requestTemplate.header("Authorization", "Bearer " + token);
        } else {
            logger.error("No authentication credentials found, EM Authorization header NOT attached");
        }
    }
}