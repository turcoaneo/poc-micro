package com.poc.microservices.main.app.controller;

import com.poc.microservices.main.app.aop.MainAppAuthorize;
import com.poc.microservices.main.app.model.MASUserRole;
import com.poc.microservices.main.app.model.dto.graphql.GraphQLMASEmployerDTO;
import com.poc.microservices.main.app.service.MASGraphQLService;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/graphql")
@Tag(name = "MAS Employer Management", description = "Using Spring graphQL API")
public class MASGraphQLController {
    private static final Logger logger = LoggerFactory.getLogger(MASGraphQLController.class);

    private final MASGraphQLService service;

    public MASGraphQLController(MASGraphQLService service) {
        this.service = service;
    }

    @GetMapping("/employer/{id}")
    @Operation(summary = "Fetch employer graphQL data from EM & EEM")
    @ApiResponse(responseCode = "200", description = "Employer EM & EEM data retrieved successfully")
    @MainAppAuthorize({MASUserRole.ADMIN})
    @SecurityRequirement(name = "BearerAuth")
    @Observed(name = "mas.getEmployer")
    public ResponseEntity<GraphQLMASEmployerDTO> getEmployer(@PathVariable Long id) {
        logger.info("Controller fetching employees working hours for employer {}", id);
        GraphQLMASEmployerDTO dto = service.getEmployerById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/employers")
    @Operation(summary = "Fetch employers graphQL data from EM only")
    @ApiResponse(responseCode = "200", description = "Employer EM data retrieved successfully")
    @SecurityRequirement(name = "BearerAuth")
    @MainAppAuthorize({MASUserRole.ADMIN})
    public ResponseEntity<List<GraphQLMASEmployerDTO>> getEmployers() {
        List<GraphQLMASEmployerDTO> dto = service.getEmployers();
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }
}