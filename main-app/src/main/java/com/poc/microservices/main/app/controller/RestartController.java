package com.poc.microservices.main.app.controller;

import com.poc.microservices.main.app.aop.MainAppAuthorize;
import com.poc.microservices.main.app.model.MASUserRole;
import com.poc.microservices.main.app.model.dto.RestartRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Tag(name = "MAS Admin Management", description = "Restart service jar")
@RestController
@RequestMapping("/admin/restart")
public class RestartController {

    @PostMapping
    @Operation(summary = "Restart service")
    @ApiResponse(responseCode = "200", description = "Restarted service successfully")
    @MainAppAuthorize({MASUserRole.ADMIN})
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<?> restartService(@RequestBody RestartRequest request) {
        String[] cmd = {
                request.getExePath(),
                request.getFilePath(),
                request.getServiceName(),
                request.getJarName(),
                request.getPort(),
                request.getHealthPath()
        };

        try {
            Process process = new ProcessBuilder(cmd)
                    .redirectErrorStream(true)
                    .start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            reader.lines().forEach(System.out::println);

            return ResponseEntity.ok("Restart triggered for " + request.getServiceName());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to restart: " + e.getMessage());
        }
    }
}