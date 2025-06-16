package com.poc.microservices.employee.app.controller;

import com.poc.microservices.employee.app.aop.EmployeeAuthorize;
import com.poc.microservices.employee.app.model.EEMUserRole;
import com.poc.microservices.employee.app.proto.GrpcClientService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "EEM GRPC", description = "Connection to EM")
@RestController("/grpc")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GreeterController {

    private final GrpcClientService greeterClient;


    @EmployeeAuthorize({EEMUserRole.ADMIN})
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/greet")
    public String greet(@RequestParam String name) {
        return greeterClient.sayHello(name);
    }
}