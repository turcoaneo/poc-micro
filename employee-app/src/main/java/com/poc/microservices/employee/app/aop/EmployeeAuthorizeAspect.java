package com.poc.microservices.employee.app.aop;

import com.poc.microservices.employee.app.model.EEMUserRole;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class EmployeeAuthorizeAspect {

    @Before("@annotation(employeeAuthorize)")
    public void checkAuthorization(EmployeeAuthorize employeeAuthorize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new EEMAuthorizationException("Unauthorized access", HttpStatus.UNAUTHORIZED);
        }

        String role = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", "")) // Extract enum-compatible role name
                .findFirst().orElse("");

        boolean hasAccess = Arrays.stream(employeeAuthorize.value())
                .map(EEMUserRole::name)
                .anyMatch(role::equals);

        if (!hasAccess) {
            throw new EEMAuthorizationException("Forbidden access", HttpStatus.FORBIDDEN);
        }
    }
}