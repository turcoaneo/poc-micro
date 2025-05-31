package com.poc.microservices.user.authentication.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import com.poc.microservices.user.authentication.model.entity.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class UserAuthorizeAspect {

    @Before("@annotation(userAuthorize)")
    public void checkAuthorization(UserAuthorize userAuthorize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthorizationException("Unauthorized access", HttpStatus.UNAUTHORIZED);
        }

        String role = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", "")) // Extract enum-compatible role name
                .findFirst().orElse("");

        boolean hasAccess = Arrays.stream(userAuthorize.value())
                .map(UserRole::name)
                .anyMatch(role::equals);

        if (!hasAccess) {
            throw new AuthorizationException("Forbidden access", HttpStatus.FORBIDDEN);
        }


    }
}