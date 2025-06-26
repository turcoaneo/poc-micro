package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.feign.EMWorkingHoursClient;
import com.poc.microservices.employer.app.model.dto.EMJobWorkingHoursDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursRequestDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class EMWorkingHoursServiceTest {

    @Mock
    private EMWorkingHoursClient client;

    @InjectMocks
    private EMWorkingHoursService service;

    @Test
    void testGetWorkingHours_successfulCall() {
        EMWorkingHoursRequestDTO request = new EMWorkingHoursRequestDTO();
        EMWorkingHoursResponseDTO expectedResponse = new EMWorkingHoursResponseDTO();
        expectedResponse.setJobWorkingHoursDTOS(Set.of(new EMJobWorkingHoursDTO(1L, 1L, 10)));

        Mockito.when(client.getWorkingHours(request)).thenReturn(expectedResponse);

        EMWorkingHoursResponseDTO result = service.getWorkingHours(request);

        Assertions.assertEquals(expectedResponse, result);
    }

    @Test
    void testFallbackTriggered_whenClientFails() {
        EMWorkingHoursRequestDTO request = new EMWorkingHoursRequestDTO();

        EMWorkingHoursResponseDTO result = service.getWorkingHours(request);

        Assertions.assertNull(result, "Fallback result should contain dummy -1 values");
    }
}