package com.poc.microservices.main.app.feign;

import com.poc.microservices.main.app.model.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "uam-service", path = "/uam/users")
public interface MASUserClient {

    @PostMapping("/register")
    ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO user);

    @PostMapping("/login")
    ResponseEntity<String> login(@RequestParam String username, @RequestParam String password);
}