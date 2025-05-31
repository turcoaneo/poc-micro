package com.poc.microservices.user.authentication.aop;

import com.poc.microservices.user.authentication.model.entity.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // Apply to methods
@Retention(RetentionPolicy.RUNTIME) // Available at runtime
public @interface UserAuthorize {
    UserRole[] value(); // Role required
}