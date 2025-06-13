package com.poc.microservices.employee.app.aop;


import com.poc.microservices.employee.app.model.EEMUserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // Apply to methods
@Retention(RetentionPolicy.RUNTIME) // Available at runtime
public @interface EmployeeAuthorize {
    EEMUserRole[] value(); // Role required
}