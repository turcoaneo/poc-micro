package com.poc.microservices.employee.app.controller;

import com.poc.microservices.employee.app.aop.EmployerAuthorize;
import com.poc.microservices.employee.app.model.EEMUserRole;
import com.poc.microservices.employee.app.model.dto.EmployeeDTO;
import com.poc.microservices.employee.app.service.EmployeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "EM User Management", description = "User authentication API")
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @EmployerAuthorize({EEMUserRole.ADMIN})
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByName(@PathVariable String name) {
        List<EmployeeDTO> employees = employeeService.getEmployeesByName(name);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByJobId(@PathVariable Long jobId) {
        return ResponseEntity.ok(employeeService.getEmployeesByJobId(jobId));
    }

    @GetMapping("/employer/{employerId}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByEmployerId(@PathVariable Long employerId) {
        return ResponseEntity.ok(employeeService.getEmployeesByEmployerId(employerId));
    }

}