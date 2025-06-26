package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.feign.EMWorkingHoursClient;
import com.poc.microservices.employer.app.model.dto.EMJobWorkingHoursDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursRequestDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursResponseDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EMWorkingHoursService {
    private static final Logger logger = LoggerFactory.getLogger(EMWorkingHoursService.class);

    private final EMWorkingHoursClient client;

    @CircuitBreaker(name = "eemCircuitBreaker", fallbackMethod = "fallbackWorkingHours")
    public EMWorkingHoursResponseDTO getWorkingHours(EMWorkingHoursRequestDTO request) {
        return client.getWorkingHours(request);
    }

    @SuppressWarnings("unused")
    public EMWorkingHoursResponseDTO fallbackWorkingHours(EMWorkingHoursRequestDTO request, Throwable ex) {
        logger.warn("Fallback triggered for getWorkingHours! Injecting dummy values. EmployerId: {}, reason: {}",
                request.getEmployerId(),
                ex.getMessage());

        Set<EMJobWorkingHoursDTO> dummyHours = Set.of(
                new EMJobWorkingHoursDTO(-1L, -1L, -1) // clearly marked as fallback
        );

        EMWorkingHoursResponseDTO fallback = new EMWorkingHoursResponseDTO();

        fallback.setJobWorkingHoursDTOS(dummyHours);
        return fallback;
    }
}