package com.poc.microservices.employer.app.controller;

import com.poc.microservices.employer.app.aop.EmployerAuthorize;
import com.poc.microservices.employer.app.model.EMUserRole;
import com.poc.microservices.employer.app.model.dto.EMGenericResponseDTO;
import com.poc.microservices.employer.app.model.dto.EmployeeDTO;
import com.poc.microservices.employer.app.model.dto.EmployerDTO;
import com.poc.microservices.employer.app.model.dto.EmployerEmployeeAssignmentPatchDTO;
import com.poc.microservices.employer.app.model.dto.JobDTO;
import com.poc.microservices.employer.app.service.EmployerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;

@Tag(name = "Employer Management", description = "Employer API")
@RestController
@RequestMapping("/api/employers")
public class EMEmployerController {
    private static final Logger logger = LoggerFactory.getLogger(EMEmployerController.class);

    private final EmployerService employerService;

    public EMEmployerController(EmployerService employerService) {
        this.employerService = employerService;
    }

    @DeleteMapping("/{employerId}")
    @EmployerAuthorize({EMUserRole.ADMIN})
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Void> deleteEmployer(@PathVariable Long employerId) {
        employerService.deleteEmployer(employerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @EmployerAuthorize({EMUserRole.ADMIN})
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<EMGenericResponseDTO> createEmployer(@RequestBody EmployerDTO employerDTO) {
        Long employerId = employerService.createEmployer(employerDTO);
        logger.info("EM Create employer {}", employerDTO.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new EMGenericResponseDTO(employerId, "Employer successfully created"));
    }

    @PutMapping("/{id}")
    @EmployerAuthorize({EMUserRole.ADMIN, EMUserRole.EMPLOYER})
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<EmployerDTO> updateEmployer(@RequestBody EmployerDTO employerDTO) {
        return ResponseEntity.ok(employerService.updateEmployer(employerDTO));
    }

    @PatchMapping("/employer/employees")
    @EmployerAuthorize({EMUserRole.ADMIN, EMUserRole.EMPLOYER})
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<EMGenericResponseDTO> assignEmployeeToJobs(@RequestBody EmployerEmployeeAssignmentPatchDTO patchDTO) {
        EMGenericResponseDTO updated = employerService.assignEmployeeToJobs(patchDTO);
        return ResponseEntity.ok(updated);
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

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test EM");
    }
}