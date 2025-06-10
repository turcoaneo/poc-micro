package com.poc.microservices.employer.app.aop;


import com.poc.microservices.employer.app.model.EMUserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // Apply to methods
@Retention(RetentionPolicy.RUNTIME) // Available at runtime
public @interface EmployerAuthorize {
    EMUserRole[] value(); // Role required
}