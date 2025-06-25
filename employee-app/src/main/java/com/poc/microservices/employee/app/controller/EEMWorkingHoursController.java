package com.poc.microservices.employee.app.controller;

import com.poc.microservices.employee.app.model.dto.WorkingHoursRequestDTO;
import com.poc.microservices.employee.app.model.dto.WorkingHoursResponseDTO;
import com.poc.microservices.employee.app.service.EEMWorkingHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/working-hours")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EEMWorkingHoursController {

    private final EEMWorkingHoursService EEMWorkingHoursService;

    @PostMapping
    public ResponseEntity<WorkingHoursResponseDTO> getWorkingHours(@RequestBody WorkingHoursRequestDTO dto) {
        Set<Long> jobIds = dto.getJobIds() == null ? new HashSet<>() : dto.getJobIds();
        WorkingHoursResponseDTO response = EEMWorkingHoursService.getWorkingHours(
            dto.getEmployerId(),
            new ArrayList<>(jobIds)
        );

        return ResponseEntity.ok(response);
    }
}