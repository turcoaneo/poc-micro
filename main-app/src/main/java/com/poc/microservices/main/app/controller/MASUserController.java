package com.poc.microservices.main.app.controller;

import com.poc.microservices.main.app.feign.UserClient;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mas-users")
public class MASUserController {
    private static final Logger logger = LoggerFactory.getLogger(MASUserController.class);

    private final UserClient userClient;

    @Autowired
    public MASUserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @Operation(summary = "Externally register user")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO user) {
        ResponseEntity<UserDTO> externalLoginToken;
        try {
            externalLoginToken = userClient.registerUser(user);
            return ResponseEntity.ok(externalLoginToken.getBody());
        } catch (FeignException feignException) {
            logger.error("Wrong feign client call", feignException);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserDTO());
        } catch (Exception exception){
            return ResponseEntity.unprocessableEntity().body(new UserDTO());
        }
    }

    @Operation(summary = "Authenticate externally user and return JWT")
    @ApiResponse(responseCode = "200", description = "JWT token returned successfully")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        ResponseEntity<String> externalLoginToken;
        try {
            externalLoginToken = userClient.login(username, password);
            return ResponseEntity.ok(externalLoginToken.getBody());
        } catch (FeignException feignException) {
            logger.error("Wrong feign client call", feignException);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception exception){
            return ResponseEntity.unprocessableEntity().body("Unknown login exception");
        }
    }

    @Operation(summary = "Fetch employer data from UAM")
    @ApiResponse(responseCode = "200", description = "Employer data retrieved successfully")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/fetch-employer")
    public ResponseEntity<String> fetchEmployer(@RequestHeader("Authorization") String token) {
        try {
            ResponseEntity<String> response = userClient.getEmployer(token);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (FeignException feignException) {
            logger.error("Feign error while fetching employer: {}", feignException.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to fetch employer data");
        } catch (Exception exception) {
            logger.error("Unexpected error fetching employer", exception);
            return ResponseEntity.internalServerError().body("Unexpected error occurred");
        }
    }

    @GetMapping("/test")
    public String test() {
        return "MAS is running!";
    }
}