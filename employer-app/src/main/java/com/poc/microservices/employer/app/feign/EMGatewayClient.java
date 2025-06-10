package com.poc.microservices.employer.app.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "api-gateway")
public interface EMGatewayClient {

    @GetMapping("/uam-service/uam/users/employer")
    ResponseEntity<String> getEmployer();
}