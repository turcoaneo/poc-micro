package com.poc.microservices.main.app.controller;

import com.poc.microservices.main.app.feign.MASGatewayClient;
import com.poc.microservices.main.app.model.dto.MASResponse;
import com.poc.microservices.main.app.model.dto.UserDTO;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mas-gateway")
public class MASGatewayController {
    private static final Logger logger = LoggerFactory.getLogger(MASGatewayController.class);

    private final MASGatewayClient MASGatewayClient;

    @Autowired
    public MASGatewayController(MASGatewayClient MASGatewayClient) {
        this.MASGatewayClient = MASGatewayClient;
    }

    @Operation(summary = "Fetch employer data from UAM")
    @ApiResponse(responseCode = "200", description = "Employer data retrieved successfully")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/test-employer-role")
    public ResponseEntity<String> testEmployerRole() {
        try {
            ResponseEntity<String> response = MASGatewayClient.getTestEmployerRole();
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (FeignException feignException) {
            logger.error("Feign error while fetching employer: {}", feignException.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to fetch employer data");
        } catch (Exception exception) {
            logger.error("Unexpected error fetching employer", exception);
            return ResponseEntity.internalServerError().body("Unexpected error occurred");
        }
    }

    @Operation(summary = "Fetch user from UAM")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/fetch-user/{username}")
    public ResponseEntity<MASResponse<UserDTO>> fetchUser(@PathVariable String username) {
        try {
            ResponseEntity<UserDTO> response = MASGatewayClient.getUser(username);
            return ResponseEntity.ok(new MASResponse<>(true, response.getBody(), "User retrieved successfully"));
        } catch (FeignException feignException) {
            logger.error("Feign error fetching user: {}", feignException.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MASResponse<>(false, null, "User retrieval failed"));
        } catch (Exception exception) {
            logger.error("Unexpected error fetching user", exception);
            return ResponseEntity.internalServerError().body(new MASResponse<>(false, null, "Unexpected error occurred"));
        }
    }
}