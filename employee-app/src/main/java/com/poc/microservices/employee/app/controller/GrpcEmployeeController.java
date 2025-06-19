package com.poc.microservices.employee.app.controller;

import com.poc.microservices.employee.app.aop.EmployeeAuthorize;
import com.poc.microservices.employee.app.model.EEMUserRole;
import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDtoList;
import com.poc.microservices.employee.app.proto.GrpcClientGreeterService;
import com.poc.microservices.employee.app.proto.GrpcEmployeeClientService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "EEM GRPC", description = "Connection to EM")
@RestController("/grpc")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GrpcEmployeeController {

    private final GrpcClientGreeterService greeterClient;
    private final GrpcEmployeeClientService grpcEmployeeClientService;


    @EmployeeAuthorize({EEMUserRole.ADMIN})
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/greet")
    public String greet(@RequestParam String name) {
        return greeterClient.sayHello(name);
    }

    @EmployeeAuthorize({EEMUserRole.ADMIN})
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/employer-jobs")
    public GrpcEmployerJobDtoList getEmployerJobs(@RequestParam List<Integer> employeeIds) {
        return grpcEmployeeClientService.getEmployerJobInfo(employeeIds);
    }


}