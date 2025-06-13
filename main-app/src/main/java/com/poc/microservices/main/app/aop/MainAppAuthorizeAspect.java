package com.poc.microservices.main.app.aop;

import com.poc.microservices.main.app.model.MASUserRole;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class MainAppAuthorizeAspect {

    @Before("@annotation(mainAppAuthorize)")
    public void checkAuthorization(MainAppAuthorize mainAppAuthorize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new MASAuthorizationException("Unauthorized access", HttpStatus.UNAUTHORIZED);
        }

        String role = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", "")) // Extract enum-compatible role name
                .findFirst().orElse("");

        boolean hasAccess = Arrays.stream(mainAppAuthorize.value())
                .map(MASUserRole::name)
                .anyMatch(role::equals);

        if (!hasAccess) {
            throw new MASAuthorizationException("Forbidden access", HttpStatus.FORBIDDEN);
        }
    }
}