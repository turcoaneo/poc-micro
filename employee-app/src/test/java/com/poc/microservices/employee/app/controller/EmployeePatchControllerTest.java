package com.poc.microservices.employee.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.microservices.employee.app.model.dto.EmployeePatchDTO;
import com.poc.microservices.employee.app.model.dto.EmployerEmployeeAssignmentPatchDTO;
import com.poc.microservices.employee.app.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(EEMEmployeeController.class)
class EmployeePatchControllerTest {

    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    EEMEmployeeController EEMEmployeeController;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(EEMEmployeeController).build();
    }

    @Test
    void patchEmployerEmployeeAssignment_returnsAcceptedWithPayload() throws Exception {
        var dto = new EmployerEmployeeAssignmentPatchDTO();
        dto.setEmployerId(42L);

        var empDto = new EmployeePatchDTO();
        empDto.setId(777L);
        empDto.setName("Ada");
        dto.setEmployee(empDto);

        dto.setJobIdWorkingHoursMap(Map.of(101L, 35));

        var objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Employee successfully patched"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(777));

        Mockito.verify(employeeService).patchEmployeeAssignment(Mockito.any());
    }
}