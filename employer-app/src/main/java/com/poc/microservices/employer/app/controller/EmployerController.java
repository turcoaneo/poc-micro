package com.poc.microservices.employer.app.controller;

import com.poc.microservices.employer.app.aop.EmployerAuthorize;
import com.poc.microservices.employer.app.model.EMUserRole;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.dto.EmployeeDTO;
import com.poc.microservices.employer.app.model.dto.EmployerDTO;
import com.poc.microservices.employer.app.model.dto.JobDTO;
import com.poc.microservices.employer.app.service.EmployerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@Tag(name = "Employer Management", description = "Employer API")
@RestController
@RequestMapping("/api/employers")
public class EmployerController {

    private final EmployerService employerService;

    public EmployerController(EmployerService employerService) {
        this.employerService = employerService;
    }

    @PostMapping
    @EmployerAuthorize({EMUserRole.ADMIN, EMUserRole.EMPLOYER})
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Employer> createEmployer(@RequestBody EmployerDTO employerDTO) {
        return ResponseEntity.ok(employerService.createEmployer(employerDTO));
    }

    @PutMapping("/{id}")
    @EmployerAuthorize({EMUserRole.ADMIN, EMUserRole.EMPLOYER})
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<EmployerDTO> updateEmployer(@RequestBody EmployerDTO employerDTO) {
        return ResponseEntity.ok(employerService.updateEmployer(employerDTO));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Optional<EmployerDTO>> getEmployerByName(@PathVariable String name) {
        return ResponseEntity.ok(employerService.getEmployerByName(name));
    }

    @GetMapping("/{id}/jobs")
    public ResponseEntity<Set<JobDTO>> getJobsByEmployerId(@PathVariable Long id) {
        return ResponseEntity.ok(employerService.getJobsByEmployerId(id));
    }

    @GetMapping("/{id}/employees")
    public ResponseEntity<Set<EmployeeDTO>> getEmployeesByEmployerId(@PathVariable Long id) {
        return ResponseEntity.ok(employerService.getEmployeesByEmployerId(id));
    }
}