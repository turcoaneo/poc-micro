package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.feign.EMWorkingHoursClient;
import com.poc.microservices.employer.app.model.dto.EMJobWorkingHoursDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursRequestDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursResponseDTO;
import com.poc.microservices.employer.app.repository.JobRepository;
import com.poc.microservices.employer.app.service.util.EMWorkingHoursCsvCache;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EMWorkingHoursService {
    private static final Logger logger = LoggerFactory.getLogger(EMWorkingHoursService.class);

    @Value("${eem.fallback.cache-enabled}")
    private boolean cacheEnabled;

    @Value("${eem.fallback.cache-path}")
    private String cachePath;

    private final EMWorkingHoursClient client;
    private final EMWorkingHoursCsvCache emWorkingHoursCsvCache;
    private final JobRepository jobRepository;

    @CircuitBreaker(name = "eemCircuitBreaker", fallbackMethod = "fallbackWorkingHours")
    public EMWorkingHoursResponseDTO getWorkingHours(EMWorkingHoursRequestDTO request) {
        EMWorkingHoursResponseDTO response = client.getWorkingHours(request);

        if (cacheEnabled) {
            try {
                Path path = Paths.get(cachePath);
                Path parentDir = path.getParent();

                if (parentDir != null && Files.notExists(parentDir)) {
                    Files.createDirectories(parentDir);
                }
                emWorkingHoursCsvCache.writeToCsv(Set.of(response), path, request.getEmployerId());
            } catch (IOException e) {
                logger.error("Failed to cache EEM response", e);
            }
        }

        return response;
    }

    @SuppressWarnings("unused")
    public EMWorkingHoursResponseDTO fallbackWorkingHours(EMWorkingHoursRequestDTO request, Throwable ex) {
        logger.warn("Fallback triggered for getWorkingHours! EmployerId: {}, reason: {}",
                request.getEmployerId(), ex.getMessage());

        EMWorkingHoursResponseDTO fallback = new EMWorkingHoursResponseDTO();

        try {
            Path path = Paths.get(cachePath).toAbsolutePath().normalize();

            if (Files.exists(path)) {
                if (CollectionUtils.isEmpty(request.getJobIds())) {
                    Set<Long> jobIds = jobRepository.findJobIdsByEmployerId(request.getEmployerId());
                    request.setJobIds(jobIds);
                }
                Set<EMJobWorkingHoursDTO> cached = emWorkingHoursCsvCache.readFromCsv(path, request);
                fallback.setJobWorkingHoursDTOS(cached);
                logger.info("Fallback response loaded from cache file: {}", path);
            } else {
                logger.warn("No cache file found at {} — injecting dummy values", path);
                fallback.setJobWorkingHoursDTOS(getDummyResponse());
            }

        } catch (Exception e) {
            logger.error("Failed to load cached fallback data", e);
            fallback.setJobWorkingHoursDTOS(getDummyResponse());
        }

        fallback.setEmployerId(request.getEmployerId());
        return fallback;
    }

    private Set<EMJobWorkingHoursDTO> getDummyResponse() {
        return Set.of(new EMJobWorkingHoursDTO(-1L, -1L, -1));
    }
}