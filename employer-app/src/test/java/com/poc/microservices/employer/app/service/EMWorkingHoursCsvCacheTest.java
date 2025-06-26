package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.model.dto.EMJobWorkingHoursDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursRequestDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursResponseDTO;
import com.poc.microservices.employer.app.service.util.EMWorkingHoursCsvCache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class EMWorkingHoursCsvCacheTest {

    @Spy
    EMWorkingHoursCsvCache emWorkingHoursCsvCache;

    private static final Path TEST_FILE = Path.of("target/test-working-hours.csv");

    @Test
    void writeAndReadFromCsv_shouldPersistAndFilterCorrectly() throws IOException {
        Long employerId = 99L;
        Set<EMWorkingHoursResponseDTO> original = Set.of(
            new EMWorkingHoursResponseDTO(employerId, Set.of(new EMJobWorkingHoursDTO(11L, 1L, 11))),
            new EMWorkingHoursResponseDTO(employerId, Set.of(new EMJobWorkingHoursDTO(22L, 2L, 22)))
        );


        emWorkingHoursCsvCache.writeToCsv(original, TEST_FILE, employerId);

        EMWorkingHoursRequestDTO request = new EMWorkingHoursRequestDTO(employerId, Set.of(11L, 22L));

        Set<EMJobWorkingHoursDTO> read = emWorkingHoursCsvCache.readFromCsv(TEST_FILE, request);

        Assertions.assertEquals(2, read.size());
        Assertions.assertTrue(read.contains(new EMJobWorkingHoursDTO(11L, 1L, 11)));
        Assertions.assertTrue(read.contains(new EMJobWorkingHoursDTO(22L, 2L, 22)));
    }
}