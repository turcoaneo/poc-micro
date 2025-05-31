package com.poc.microservices.user.authentication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.poc.microservices.user.authentication.aop.UserAuthorize;
import com.poc.microservices.user.authentication.model.dto.UserDTO;
import com.poc.microservices.user.authentication.model.entity.UserRole;
import com.poc.microservices.user.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Management", description = "User authentication and management API")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public UserDTO registerUser(@RequestBody UserDTO user) {
        return userService.saveUser(user);
    }

    @Operation(summary = "Fetch a user by username")
    @SecurityRequirement(name = "BearerAuth")
    @UserAuthorize({UserRole.ADMIN})
    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @Operation(summary = "Test Employer and Admin roles authorization")
    @SecurityRequirement(name = "BearerAuth")
    @UserAuthorize({UserRole.ADMIN, UserRole.EMPLOYER})
    @GetMapping("/employer")
    public ResponseEntity<String> getTestEmployer() {
        return ResponseEntity.ok("Test employer");
    }

    @Operation(summary = "Authenticate user and return JWT")
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        return userService.authenticateUser(username, password);
    }
}