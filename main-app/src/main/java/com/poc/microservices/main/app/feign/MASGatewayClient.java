package com.poc.microservices.main.app.feign;

import com.poc.microservices.main.app.model.dto.MASEmployeeDTO;
import com.poc.microservices.main.app.model.dto.MASEmployerDTO;
import com.poc.microservices.main.app.model.dto.MASGenericResponseDTO;
import com.poc.microservices.main.app.model.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "api-gateway")
public interface MASGatewayClient {

    @GetMapping("/uam-service/uam/users/test-employer-role")
    ResponseEntity<String> getTestEmployerRole();

    @GetMapping("/uam-service/uam/users/{username}")
    ResponseEntity<UserDTO> getUser(@PathVariable String username);

    @PostMapping("/em-service/em/api/employers")
    ResponseEntity<MASGenericResponseDTO> createEmployer(@RequestBody MASEmployerDTO employerDTO);

    @PostMapping("/eem-service/eem/api/employees")
    ResponseEntity<MASGenericResponseDTO> createEmployee(MASEmployeeDTO employeeDTO);
}