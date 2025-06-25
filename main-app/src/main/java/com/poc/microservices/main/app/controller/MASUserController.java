package com.poc.microservices.main.app.controller;

import com.poc.microservices.main.app.feign.MASUserClient;
import com.poc.microservices.main.app.model.dto.UserDTO;
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

@Tag(name = "MAS UAM Management", description = "Requiring A&A")
@RestController
@RequestMapping("/mas-users")
public class MASUserController {
    private static final Logger logger = LoggerFactory.getLogger(MASUserController.class);

    private final MASUserClient masUserClient;

    @Autowired
    public MASUserController(MASUserClient masUserClient) {
        this.masUserClient = masUserClient;
    }

    @Operation(summary = "Externally register user")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO user) {
        ResponseEntity<UserDTO> externalLoginToken;
        try {
            externalLoginToken = masUserClient.registerUser(user);
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
            externalLoginToken = masUserClient.login(username, password);
            return ResponseEntity.ok(externalLoginToken.getBody());
        } catch (FeignException feignException) {
            logger.error("Wrong feign client call", feignException);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception exception) {
            return ResponseEntity.unprocessableEntity().body("Unknown login exception");
        }
    }

    @GetMapping("/test")
    public String test() {
        return "MAS is running!";
    }
}