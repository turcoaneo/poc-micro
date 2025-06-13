package com.poc.microservices.main.app.aop;


import com.poc.microservices.main.app.model.MASUserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // Apply to methods
@Retention(RetentionPolicy.RUNTIME) // Available at runtime
public @interface MainAppAuthorize {
    MASUserRole[] value(); // Role required
}