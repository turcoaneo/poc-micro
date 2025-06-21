package com.poc.microservices.employee.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "EEM Actuator Test", description = "Employee Actuator Test")
@RestController
@RequestMapping("/api/employees")
@RefreshScope
public class ActuatorTestController {

    @Value("${eem.scheduler.enabled}")
    private boolean isSchedulerEnabled;

    @GetMapping("/actuator/test/")
    public ResponseEntity<String> testProperties() {
        return ResponseEntity.ok(String.valueOf(isSchedulerEnabled));
    }
}