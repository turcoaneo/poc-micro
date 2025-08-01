package com.poc.microservices.employee.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.microservices.employee.app.model.dto.EmployeeDTO;
import com.poc.microservices.employee.app.model.dto.EmployerDTO;
import com.poc.microservices.employee.app.model.dto.JobDTO;
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

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(EEMEmployeeController.class)
class EEMEmployeeControllerTest {

    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    EEMEmployeeController EEMEmployeeController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(EEMEmployeeController).build();
    }

    @Test
    void testCreateEmployee() throws Exception {
        EmployeeDTO dto = new EmployeeDTO(null, "Alice", List.of(
                new EmployerDTO(1L, "TechCorp", List.of(new JobDTO(101L, "Developer", 40), new JobDTO(102L,
                        "Architect", 40))),
                new EmployerDTO(2L, "DataLabs", List.of(new JobDTO(201L, "Analyst", 40), new JobDTO(202L, "ML " +
                        "Engineer", 40)))
        ));

        Mockito.when(employeeService.createEmployee(Mockito.any(EmployeeDTO.class))).thenReturn(0L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Employee successfully created"));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        EmployeeDTO dto = new EmployeeDTO(null, "Alice", List.of());

        Mockito.when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Alice"));
    }

    @Test
    void testGetEmployeesByName() throws Exception {
        List<EmployeeDTO> employees = List.of(new EmployeeDTO(null, "Alice", List.of()),
                new EmployeeDTO(null, "Alice", List.of()));

        Mockito.when(employeeService.getEmployeesByName("Alice")).thenReturn(employees);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/name/Alice"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Alice"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Alice"));
    }

    @Test
    void testGetEmployeesByJobId() throws Exception {
        List<EmployeeDTO> employees = List.of(
                new EmployeeDTO(null, "Alice", List.of()),
                new EmployeeDTO(null, "Bob", List.of())
        );

        Mockito.when(employeeService.getEmployeesByJobId(101L)).thenReturn(employees);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/job/101"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Alice"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Bob"));
    }

    @Test
    void testGetEmployeesByEmployerId() throws Exception {
        List<EmployeeDTO> employees = List.of(new EmployeeDTO(null, "Charlie", List.of())
        );

        Mockito.when(employeeService.getEmployeesByEmployerId(1L)).thenReturn(employees);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/employer/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Charlie"));
    }

}