package com.poc.microservices.employee.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.microservices.employee.app.model.dto.JobWorkingHoursDTO;
import com.poc.microservices.employee.app.model.dto.WorkingHoursRequestDTO;
import com.poc.microservices.employee.app.model.dto.WorkingHoursResponseDTO;
import com.poc.microservices.employee.app.service.EEMWorkingHoursService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebMvcTest(EEMWorkingHoursController.class)
class EEMWorkingHoursControllerTest {

    private MockMvc mockMvc;

    @MockitoBean
    private EEMWorkingHoursService service;

    @Autowired
    EEMWorkingHoursController EEMWorkingHoursController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(EEMWorkingHoursController).build();
    }

    @Test
    void shouldReturnWorkingHoursResponseDTO() throws Exception {
        WorkingHoursRequestDTO requestDTO = new WorkingHoursRequestDTO();
        requestDTO.setEmployeeId(1L);
        requestDTO.setEmployerId(2L);
        requestDTO.setJobIds(Set.of(101L, 102L));

        List<JobWorkingHoursDTO> jobHours = List.of(
                new JobWorkingHoursDTO(101L, 30),
                new JobWorkingHoursDTO(102L, 20)
        );
        WorkingHoursResponseDTO responseDTO = new WorkingHoursResponseDTO();
        responseDTO.setEmployeeId(1L);
        responseDTO.setEmployerId(2L);
        responseDTO.setJobWorkingHoursDTOS(new HashSet<>(jobHours));

        Mockito.when(service.getWorkingHours(1L, 2L, List.of(101L, 102L))).thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/working-hours")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employeeId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerId").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobWorkingHoursDTOS[?(@.jobId == 101)].workingHours").value(30));

        Mockito.verify(service).getWorkingHours(1L, 2L, List.of(101L, 102L));
    }
}