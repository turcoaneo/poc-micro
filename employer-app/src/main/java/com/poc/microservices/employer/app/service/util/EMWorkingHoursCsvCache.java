package com.poc.microservices.employer.app.service.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.poc.microservices.employer.app.model.dto.EMJobWorkingHoursDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursRequestDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursResponseDTO;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
public class EMWorkingHoursCsvCache {

    public void writeToCsv(Set<EMWorkingHoursResponseDTO> data, Path filePath, Long employerId) throws IOException {
        try (Writer writer = Files.newBufferedWriter(filePath);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            csvWriter.writeNext(new String[]{"employerId", "jobId", "employeeId", "hours"});

            for (EMWorkingHoursResponseDTO dto : data) {
                for (EMJobWorkingHoursDTO jobWorkingHoursDTO : dto.getJobWorkingHoursDTOS()) {
                    csvWriter.writeNext(new String[]{
                            employerId.toString(),
                            jobWorkingHoursDTO.getJobId().toString(),
                            jobWorkingHoursDTO.getEmployeeId().toString(),
                            String.valueOf(jobWorkingHoursDTO.getWorkingHours())
                    });
                }
            }
        }
    }

    public Set<EMJobWorkingHoursDTO> readFromCsv(Path filePath, EMWorkingHoursRequestDTO request) throws IOException {
        Set<Long> jobIds = request.getJobIds();
        Set<EMJobWorkingHoursDTO> result = new HashSet<>();

        try (Reader reader = Files.newBufferedReader(filePath);
             CSVReader csvReader = new CSVReader(reader)) {

            csvReader.readNext(); // skip header

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                Long employerId = Long.parseLong(nextLine[0]);
                Long jobId = Long.parseLong(nextLine[1]);

                if (!Objects.equals(employerId, request.getEmployerId()) || !jobIds.contains(jobId)) {
                    continue;
                }

                Long employeeId = Long.parseLong(nextLine[2]);
                int hours = -1 * Integer.parseInt(nextLine[3]);//negative cached value to differentiate from real one

                result.add(new EMJobWorkingHoursDTO(jobId, employeeId, hours));
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}