package com.poc.microservices.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway")
public class GatewayCheckController {

    @GetMapping("/test")
    public String test() {
        return "Gateway controller is running!";
    }
}