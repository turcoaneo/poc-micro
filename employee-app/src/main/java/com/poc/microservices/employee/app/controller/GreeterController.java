package com.poc.microservices.employee.app.controller;

import com.poc.microservices.employee.app.proto.GrpcClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GreeterController {

    private final GrpcClientService greeterClient;

    @GetMapping("/greet")
    public String greet(@RequestParam String name) {
        return greeterClient.sayHello(name);
    }
}