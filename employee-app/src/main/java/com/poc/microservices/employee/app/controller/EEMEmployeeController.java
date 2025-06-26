package com.poc.microservices.employee.app.controller;

import com.poc.microservices.employee.app.aop.EmployeeAuthorize;
import com.poc.microservices.employee.app.model.EEMUserRole;
import com.poc.microservices.employee.app.model.dto.EEMGenericResponseDTO;
import com.poc.microservices.employee.app.model.dto.EmployeeDTO;
import com.poc.microservices.employee.app.model.dto.EmployerEmployeeAssignmentPatchDTO;
import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDto;
import com.poc.microservices.employee.app.service.EmployeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "EEM Management", description = "Employee API")
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EEMEmployeeController {

    private final EmployeeService employeeService;

    @PatchMapping
    public ResponseEntity<EEMGenericResponseDTO> patchEmployerEmployeeAssignment(@RequestBody EmployerEmployeeAssignmentPatchDTO dto) {
        employeeService.patchEmployeeAssignment(dto);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new EEMGenericResponseDTO(dto.getEmployee().getId(), "Employee successfully patched"));
    }

    @DeleteMapping("/{employeeId}")
    @EmployeeAuthorize({EEMUserRole.ADMIN})
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<EEMGenericResponseDTO> deleteEmployee(@PathVariable Long employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new EEMGenericResponseDTO(employeeId, "Employee successfully deleted"));
    }

    @PostMapping
    @EmployeeAuthorize({EEMUserRole.ADMIN, EEMUserRole.EMPLOYER})
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<EEMGenericResponseDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Long employeeId = employeeService.createEmployee(employeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new EEMGenericResponseDTO(employeeId, "Employee successfully created"));
    }

    @PatchMapping("/reconcile")
    @EmployeeAuthorize({EEMUserRole.ADMIN, EEMUserRole.EMPLOYER})
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<EEMGenericResponseDTO> reconcileEmployee(@RequestBody GrpcEmployerJobDto dto) {
        EEMGenericResponseDTO response = employeeService.reconcileEmployee(dto);
        return ResponseEntity.ok(response);
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