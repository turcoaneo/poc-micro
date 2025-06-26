package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.feign.EMWorkingHoursClient;
import com.poc.microservices.employer.app.model.dto.EMJobWorkingHoursDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursRequestDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursResponseDTO;
import com.poc.microservices.employer.app.service.util.EMWorkingHoursCsvCache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class EMWorkingHoursServiceFallbackTest {

    @Mock
    private EMWorkingHoursClient client;

    @Mock
    private EMWorkingHoursCsvCache cache;

    @InjectMocks
    private EMWorkingHoursService service;

    private final Path testPath = Path.of("target/test-working-hours.csv");

    @BeforeEach
    void setUp() {
        Assertions.assertNotNull(client);
        ReflectionTestUtils.setField(service, "cachePath", testPath.toString());
        ReflectionTestUtils.setField(service, "cacheEnabled", true);
        try {
            Files.deleteIfExists(testPath);
        } catch (IOException ignored) {
        }
    }

    @Test
    void fallback_shouldUseCachedData_whenFileExists() throws Exception {
        EMWorkingHoursRequestDTO request = new EMWorkingHoursRequestDTO(1L, Set.of(101L));
        Set<EMJobWorkingHoursDTO> cached = Set.of(new EMJobWorkingHoursDTO(101L, -1L, 8));

        Mockito.when(cache.readFromCsv(Mockito.any(Path.class), Mockito.any(EMWorkingHoursRequestDTO.class)))
                .thenReturn(cached);
        Files.createDirectories(testPath.getParent());
        Files.createFile(testPath);

        EMWorkingHoursResponseDTO result = service.fallbackWorkingHours(request, new RuntimeException("Simulated"));

        Assertions.assertEquals(cached, result.getJobWorkingHoursDTOS());
    }

    @Test
    void fallback_shouldUseDummy_whenFileNotExists() {
        EMWorkingHoursRequestDTO request = new EMWorkingHoursRequestDTO(1L, Set.of(202L));

        EMWorkingHoursResponseDTO result = service.fallbackWorkingHours(request, new RuntimeException("Boom"));

        Set<EMJobWorkingHoursDTO> hours = result.getJobWorkingHoursDTOS();
        Assertions.assertEquals(1, hours.size());
        Assertions.assertEquals(-1, (int) hours.iterator().next().getWorkingHours());
    }

    @Test
    void fallback_shouldUseDummy_whenCacheThrowsException() {
        EMWorkingHoursRequestDTO request = new EMWorkingHoursRequestDTO(1L, Set.of(303L));

        EMWorkingHoursResponseDTO result = service.fallbackWorkingHours(request, new RuntimeException("Boom"));

        Assertions.assertTrue(result.getJobWorkingHoursDTOS().stream()
                .allMatch(j -> j.getWorkingHours() == -1));
    }
}