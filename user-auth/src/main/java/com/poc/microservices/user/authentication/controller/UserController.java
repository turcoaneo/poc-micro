package com.poc.microservices.user.authentication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.poc.microservices.user.authentication.aop.UserAuthorize;
import com.poc.microservices.user.authentication.model.dto.UserDTO;
import com.poc.microservices.user.authentication.model.entity.UserRole;
import com.poc.microservices.user.authentication.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Management", description = "User authentication and management API")
@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO user) {
        UserDTO userDTO = userService.saveUser(user);
        if (userDTO == null) {
            return ResponseEntity.badRequest().body(new UserDTO());
        }
        return ResponseEntity.ok(userDTO);
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
    @GetMapping("/test-employer-role")
    public ResponseEntity<String> getTestEmployer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("UAM Processing Authentication: {}", authentication);

        return ResponseEntity.ok("Test EMPLOYER");
    }

    @Operation(summary = "Authenticate user and return JWT")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        String jwtToken = userService.authenticateUser(username, password);

        if (jwtToken == null || jwtToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        return ResponseEntity.ok(jwtToken);
    }
}