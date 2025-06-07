package com.poc.microservices.main.app.feign;

import com.poc.microservices.main.app.model.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "api-gateway")
public interface GatewayClient {

    @GetMapping("/uam-service/uam/users/employer")
    ResponseEntity<String> getEmployer();

    @GetMapping("/uam-service/uam/users/{username}")
    ResponseEntity<UserDTO> getUser(@PathVariable String username);
}