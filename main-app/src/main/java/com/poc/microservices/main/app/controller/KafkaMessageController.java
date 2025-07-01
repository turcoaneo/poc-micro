package com.poc.microservices.main.app.controller;

import com.poc.microservices.main.app.aop.MainAppAuthorize;
import com.poc.microservices.main.app.model.MASUserRole;
import com.poc.microservices.main.app.service.KafkaEventSubscriber;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "MAS Kafka Management", description = "Consuming employee working hours")
@RestController
@RequestMapping("/api/messages")
public class KafkaMessageController {

    private final KafkaEventSubscriber subscriber;

    public KafkaMessageController(KafkaEventSubscriber subscriber) {
        this.subscriber = subscriber;
    }

    @GetMapping("/received")
    @Operation(summary = "Get message from Kafka")
    @ApiResponse(responseCode = "200", description = "Employee working hours received successfully")
    @SecurityRequirement(name = "BearerAuth")
    @MainAppAuthorize({MASUserRole.ADMIN})
    public ResponseEntity<List<String>> getReceivedMessages() {
        List<String> messages = subscriber.getReceivedMessages();
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/received")
    @Operation(summary = "Remove message from Kafka")
    @ApiResponse(responseCode = "202", description = "Employee working hours message deleted successfully")
    @SecurityRequirement(name = "BearerAuth")
    @MainAppAuthorize({MASUserRole.ADMIN})
    public ResponseEntity<String> clearMessages() {
        subscriber.getReceivedMessages().clear();
        return ResponseEntity.ok("Messages cleared");
    }
}