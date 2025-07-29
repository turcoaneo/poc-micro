package com.poc.microservices.employee.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.microservices.employee.app.aop.EmployeeAuthorize;
import com.poc.microservices.employee.app.kafka.KafkaEventPublisher;
import com.poc.microservices.employee.app.model.EEMUserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@Tag(name = "EEM Kafka Management", description = "Publishing employee working hours")
@RestController
@RequestMapping("/api/employees/events")
public class KafkaEventController {

    private final KafkaEventPublisher kafkaPublisher;

    @Autowired
    public KafkaEventController(KafkaEventPublisher kafkaPublisher) {
        this.kafkaPublisher = kafkaPublisher;
    }

    @PostMapping("/publish")
    @Operation(summary = "Post message to Kafka")
    @ApiResponse(responseCode = "201", description = "Employee working hours published successfully")
    @SecurityRequirement(name = "BearerAuth")
    @EmployeeAuthorize({EEMUserRole.ADMIN, EEMUserRole.EMPLOYEE})
    public ResponseEntity<String> publishEvent(@RequestBody Map<String, Object> payload) {
        try {
            String key = payload.getOrDefault("messageId", UUID.randomUUID().toString()).toString();
            String event = new ObjectMapper().writeValueAsString(payload);
            if (kafkaPublisher == null || kafkaPublisher.getProducer() == null) {
                return ResponseEntity.ok("Event publishing is stopped");
            }
            kafkaPublisher.publishEvent(key, event);
            return ResponseEntity.ok("Event published successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to publish event: " + e.getMessage());
        }
    }
}