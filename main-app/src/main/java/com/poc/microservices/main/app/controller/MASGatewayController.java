package com.poc.microservices.main.app.controller;

import com.poc.microservices.main.app.aop.MainAppAuthorize;
import com.poc.microservices.main.app.feign.MASGatewayClient;
import com.poc.microservices.main.app.model.MASUserRole;
import com.poc.microservices.main.app.model.dto.MASEmployerEmployeeAssignmentPatchDTO;
import com.poc.microservices.main.app.model.dto.MASEmployeeDTO;
import com.poc.microservices.main.app.model.dto.MASEmployerDTO;
import com.poc.microservices.main.app.model.dto.MASGenericResponseDTO;
import com.poc.microservices.main.app.model.dto.MASResponse;
import com.poc.microservices.main.app.model.dto.UserDTO;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "MAS API Gateway Management", description = "Using API Gateway")
@RestController
@RequestMapping("/mas-gateway")
public class MASGatewayController {
    private static final Logger logger = LoggerFactory.getLogger(MASGatewayController.class);

    private final MASGatewayClient masGatewayClient;

    @Autowired
    public MASGatewayController(MASGatewayClient MASGatewayClient) {
        this.masGatewayClient = MASGatewayClient;
    }

    @Operation(summary = "Fetch test employer role data from UAM")
    @ApiResponse(responseCode = "200", description = "Employer data retrieved successfully")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/test-employer-role")
    public ResponseEntity<String> testEmployerRole() {
        try {
            ResponseEntity<String> response = masGatewayClient.getTestEmployerRole();
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (FeignException feignException) {
            logger.error("Feign error while fetching employer: {}", feignException.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to fetch employer data");
        } catch (Exception exception) {
            logger.error("Unexpected error fetching employer", exception);
            return ResponseEntity.internalServerError().body("Unexpected error occurred");
        }
    }

    @Operation(summary = "Fetch user role from UAM")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/fetch-user-role/{username}")
    public ResponseEntity<MASResponse<UserDTO>> fetchUser(@PathVariable String username) {
        try {
            ResponseEntity<UserDTO> response = masGatewayClient.getUser(username);
            return ResponseEntity.ok(new MASResponse<>(true, response.getBody(), "User retrieved successfully"));
        } catch (FeignException feignException) {
            logger.error("Feign error fetching user: {}", feignException.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MASResponse<>(false, null, "User retrieval failed"));
        } catch (Exception exception) {
            logger.error("Unexpected error fetching user", exception);
            return ResponseEntity.internalServerError().body(new MASResponse<>(false, null, "Unexpected error occurred"));
        }
    }

    @PostMapping("/create-employer")
    @Operation(summary = "Create employer in EM")
    @MainAppAuthorize({MASUserRole.ADMIN})
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<MASGenericResponseDTO> createEmployer(@RequestBody MASEmployerDTO employerDTO) {
        ResponseEntity<MASGenericResponseDTO> employerResponse = masGatewayClient.createEmployer(employerDTO);
        return ResponseEntity.status(employerResponse.getStatusCode()).body(employerResponse.getBody());
    }

    @PostMapping("/create-employee")
    @Operation(summary = "Create employee in EEM")
    @MainAppAuthorize({MASUserRole.ADMIN, MASUserRole.EMPLOYER})
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<MASGenericResponseDTO> createEmployee(@RequestBody MASEmployeeDTO employeeDTO) {
        ResponseEntity<MASGenericResponseDTO> employeeResponse = masGatewayClient.createEmployee(employeeDTO);
        return ResponseEntity.status(employeeResponse.getStatusCode()).body(employeeResponse.getBody());
    }

    @PatchMapping("/assign-employee")
    @MainAppAuthorize({MASUserRole.ADMIN, MASUserRole.EMPLOYER})
    @Operation(summary = "Assign Employee to Jobs in EM")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<MASGenericResponseDTO> assignEmployeeToJobs(
            @RequestBody MASEmployerEmployeeAssignmentPatchDTO patchDTO) {
        ResponseEntity<MASGenericResponseDTO> response = masGatewayClient.assignEmployeeToJobs(patchDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

}