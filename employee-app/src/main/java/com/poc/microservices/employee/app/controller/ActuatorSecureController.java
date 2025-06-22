package com.poc.microservices.employee.app.controller;

import com.poc.microservices.employee.app.aop.EmployeeAuthorize;
import com.poc.microservices.employee.app.model.EEMUserRole;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "EEM Actuator Management", description = "Employee Simple Actuator")
@RestController
@RequestMapping("/actuator")
public class ActuatorSecureController {

    @PostMapping("/refresh")
    @EmployeeAuthorize({EEMUserRole.ADMIN})
    @SecurityRequirement(name = "BearerAuth")
    public void refreshProperties() {}
}