package com.poc.microservices.main.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Tag(name = "MAS AWS Lambda Management", description = "Using AWS python ZIPPED lambda")
@RestController
public class LambdaController {

    private final RestTemplate restTemplate;

    @Autowired
    public LambdaController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/get-time")
    @Operation(summary = "Fetch test time")
    @ApiResponse(responseCode = "200", description = "AWS server time")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<String> getTimeFromLambda() {
        String lambdaUrl = "https://dfl4j4y4jcwaeoldttdbldtgqa0okedt.lambda-url.eu-north-1.on.aws/";
        String result = restTemplate.getForObject(lambdaUrl, String.class);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get-working-hours")
    @Operation(summary = "Fetch employer - employee - job - working hours")
    @ApiResponse(responseCode = "200", description = "AWS SQL from employer_db joining employee_db")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<String> getWorkingHoursFromLambda() {
        String lambdaUrl = "https://fk7ioxueeo47hwoxq4kztfaqjy0htunw.lambda-url.eu-north-1.on.aws/";
        String result = restTemplate.getForObject(lambdaUrl, String.class);
        return ResponseEntity.ok(result);
    }
}