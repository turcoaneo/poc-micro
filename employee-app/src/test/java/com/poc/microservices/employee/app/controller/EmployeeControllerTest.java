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
@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    void testCreateEmployee() throws Exception {
        EmployeeDTO dto = new EmployeeDTO(null, "Alice", 40, List.of(
                new EmployerDTO(1L, "TechCorp", List.of(new JobDTO(101L, "Developer"), new JobDTO(102L, "Architect"))),
                new EmployerDTO(2L, "DataLabs", List.of(new JobDTO(201L, "Analyst"), new JobDTO(202L, "ML Engineer")))
        ));

        Mockito.when(employeeService.createEmployee(Mockito.any(EmployeeDTO.class))).thenReturn(0L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(0)); // Two employers assigned
    }

    @Test
    void testGetEmployeeById() throws Exception {
        EmployeeDTO dto = new EmployeeDTO(null, "Alice", 40, List.of());

        Mockito.when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Alice"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.workingHours").value(40));
    }

    @Test
    void testGetEmployeesByName() throws Exception {
        List<EmployeeDTO> employees = List.of(new EmployeeDTO(null, "Alice", 40, List.of()),
                new EmployeeDTO(null, "Alice", 35, List.of()));

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
                new EmployeeDTO(null, "Alice", 40, List.of()),
                new EmployeeDTO(null, "Bob", 35, List.of())
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
        List<EmployeeDTO> employees = List.of(new EmployeeDTO(null, "Charlie", 42, List.of())
        );

        Mockito.when(employeeService.getEmployeesByEmployerId(1L)).thenReturn(employees);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/employees/employer/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Charlie"));
    }

}