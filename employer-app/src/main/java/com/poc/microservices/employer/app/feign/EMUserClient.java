package com.poc.microservices.employer.app.feign;

import com.poc.microservices.employer.app.model.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "uam-service", path = "/uam/users")
public interface EMUserClient {

    @PostMapping("/register")
    ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO user);

    @PostMapping("/login")
    ResponseEntity<String> login(@RequestParam String username, @RequestParam String password);
}