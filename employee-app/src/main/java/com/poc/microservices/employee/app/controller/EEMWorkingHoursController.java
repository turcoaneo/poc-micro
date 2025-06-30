package com.poc.microservices.employee.app.controller;

import com.poc.microservices.employee.app.aop.EmployeeAuthorize;
import com.poc.microservices.employee.app.model.EEMUserRole;
import com.poc.microservices.employee.app.model.dto.EEMWorkingHoursRequestDTO;
import com.poc.microservices.employee.app.model.dto.EEMWorkingHoursResponseDTO;
import com.poc.microservices.employee.app.service.EEMWorkingHoursService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Tag(name = "EEM GraphQL Management", description = "Returning employee working hours to EM")
@RestController
@RequestMapping("/working-hours")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EEMWorkingHoursController {
    private static final Logger logger = LoggerFactory.getLogger(EEMWorkingHoursController.class);

    private final EEMWorkingHoursService EEMWorkingHoursService;

    @PostMapping
    @Operation(summary = "Find employee data by feign request from EM")
    @ApiResponse(responseCode = "200", description = "Employee working hours retrieved successfully")
    @SecurityRequirement(name = "BearerAuth")
    @EmployeeAuthorize({EEMUserRole.ADMIN})
    public ResponseEntity<EEMWorkingHoursResponseDTO> getWorkingHours(@RequestBody EEMWorkingHoursRequestDTO dto) {
        logger.info("EEM fetching employees working hours for employer {}", dto.getEmployerId());
        Set<Long> jobIds = dto.getJobIds() == null ? new HashSet<>() : dto.getJobIds();
        EEMWorkingHoursResponseDTO response = EEMWorkingHoursService.getWorkingHours(
            dto.getEmployerId(),
            new ArrayList<>(jobIds)
        );

        return ResponseEntity.ok(response);
    }
}