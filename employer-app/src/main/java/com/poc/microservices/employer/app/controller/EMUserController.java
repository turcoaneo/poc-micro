package com.poc.microservices.employer.app.controller;

import com.poc.microservices.employer.app.feign.EMUserClient;
import com.poc.microservices.employer.app.model.dto.UserDTO;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "EM User Management", description = "User authentication API")
@RestController
@RequestMapping("/em-users")
public class EMUserController {
    private static final Logger logger = LoggerFactory.getLogger(EMUserController.class);

    private final EMUserClient emUserClient;

    @Autowired
    public EMUserController(EMUserClient emUserClient) {
        this.emUserClient = emUserClient;
    }

    @Operation(summary = "Externally register user")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO user) {
        ResponseEntity<UserDTO> externalLoginToken;
        try {
            externalLoginToken = emUserClient.registerUser(user);
            return ResponseEntity.ok(externalLoginToken.getBody());
        } catch (FeignException feignException) {
            logger.error("Wrong feign client call", feignException);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserDTO());
        } catch (Exception exception) {
            return ResponseEntity.unprocessableEntity().body(new UserDTO());
        }
    }

    @Operation(summary = "Authenticate externally user and return JWT")
    @ApiResponse(responseCode = "200", description = "JWT token returned successfully")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        ResponseEntity<String> externalLoginToken;
        try {
            externalLoginToken = emUserClient.login(username, password);
            return ResponseEntity.ok(externalLoginToken.getBody());
        } catch (FeignException feignException) {
            logger.error("Wrong feign client call", feignException);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception exception) {
            return ResponseEntity.unprocessableEntity().body("Unknown login exception");
        }
    }
}