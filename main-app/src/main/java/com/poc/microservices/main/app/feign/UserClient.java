package com.poc.microservices.main.app.feign;

import com.poc.microservices.main.app.model.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "uam-service", url = "http://localhost:8092/users")
public interface UserClient {

    @PostMapping("/register")
    ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO user);

    @PostMapping("/login")
    ResponseEntity<String> login(@RequestParam String username, @RequestParam String password);

    @GetMapping("/employer")
    ResponseEntity<String> getEmployer();

    @GetMapping("/{username}")
    ResponseEntity<UserDTO> getUser(@PathVariable String username);
}