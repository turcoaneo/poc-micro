package com.poc.microservices.employee.app.service;

import com.poc.microservices.employee.app.model.dto.JobWorkingHoursDTO;
import com.poc.microservices.employee.app.model.dto.WorkingHoursResponseDTO;
import com.poc.microservices.employee.app.repository.EmployeeJobEmployerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class EEMWorkingHoursServiceTest {

    @InjectMocks
    private EEMWorkingHoursService EEMWorkingHoursService;

    @Mock
    private EmployeeJobEmployerRepository ejeRepository;

    @Test
    void shouldReturnWorkingHoursWhenJobsProvided() {
        Long employeeId = 1L;
        Long employerId = 2L;
        List<Long> jobIds = List.of(101L, 102L);

        List<JobWorkingHoursDTO> mockResult = List.of(
                new JobWorkingHoursDTO(101L, 1L, 40),
                new JobWorkingHoursDTO(102L, 2L, 20)
        );

        Mockito.when(ejeRepository.findWorkingHoursByEmployeeEmployerAndJobs(employerId, jobIds))
                .thenReturn(mockResult);

        WorkingHoursResponseDTO response = EEMWorkingHoursService.getWorkingHours(employerId, jobIds);

        Assertions.assertEquals(employeeId, response.getEmployeeId());
        Assertions.assertEquals(employerId, response.getEmployerId());
        Assertions.assertEquals(2, response.getJobWorkingHoursDTOS().size());
        Assertions.assertTrue(response.getJobWorkingHoursDTOS().containsAll(mockResult));
    }

    @Test
    void shouldReturnWorkingHoursWhenJobIdsNull() {
        Long employerId = 2L;

        List<JobWorkingHoursDTO> mockResult = List.of(
                new JobWorkingHoursDTO(101L, 1L, 40)
        );

        Mockito.when(ejeRepository.findWorkingHoursByEmployeeEmployerAndJobs(employerId, null))
                .thenReturn(mockResult);

        WorkingHoursResponseDTO response = EEMWorkingHoursService.getWorkingHours(employerId, null);

        Assertions.assertEquals(1, response.getJobWorkingHoursDTOS().size());
        Assertions.assertEquals(101L, response.getJobWorkingHoursDTOS().iterator().next().getJobId());
    }
}