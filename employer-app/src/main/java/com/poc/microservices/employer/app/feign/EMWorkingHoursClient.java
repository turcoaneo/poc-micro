package com.poc.microservices.employer.app.feign;

import com.poc.microservices.employer.app.model.dto.EMWorkingHoursRequestDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "eem-service", path = "/eem")
public interface EMWorkingHoursClient {

    @PostMapping("/working-hours")
    EMWorkingHoursResponseDTO getWorkingHours(@RequestBody EMWorkingHoursRequestDTO request);
}