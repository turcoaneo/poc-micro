package com.poc.microservices.employer.app.controller;

import com.poc.microservices.employer.app.aop.EmployerAuthorize;
import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.model.EMUserRole;
import com.poc.microservices.employer.app.service.EmployerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Employer Management", description = "Employer API")
@RestController
@RequestMapping("/api/employers")
public class EmployerController {

    private final EmployerService employerService;

    public EmployerController(EmployerService employerService) {
        this.employerService = employerService;
    }

    @PostMapping
//    @EmployerAuthorize({EMUserRole.ADMIN, EMUserRole.EMPLOYER})
//    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Employer> createEmployer(@RequestBody Employer employer) {
        return ResponseEntity.ok(employerService.createEmployer(employer));
    }

    @PutMapping("/{id}")
    @EmployerAuthorize({EMUserRole.ADMIN, EMUserRole.EMPLOYER})
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Employer> updateEmployer(@PathVariable Long id, @RequestBody Employer employer) {
        return ResponseEntity.ok(employerService.updateEmployer(id, employer));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Optional<Employer>> getEmployerByName(@PathVariable String name) {
        return ResponseEntity.ok(employerService.getEmployerByName(name));
    }

    @GetMapping("/{id}/jobs")
    public ResponseEntity<List<Job>> getJobsByEmployerId(@PathVariable Long id) {
        return ResponseEntity.ok(employerService.getJobsByEmployerId(id));
    }

    @GetMapping("/{id}/employees")
    public ResponseEntity<List<Employee>> getEmployeesByEmployerId(@PathVariable Long id) {
        return ResponseEntity.ok(employerService.getEmployeesByEmployerId(id));
    }
}