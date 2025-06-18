package com.poc.microservices.employee.app.controller;

import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDto;
import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDtoList;
import com.poc.microservices.employee.app.proto.GrpcClientGreeterService;
import com.poc.microservices.employee.app.proto.GrpcEmployeeClientService;
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

@ExtendWith(MockitoExtension.class)
@WebMvcTest(GrpcEmployeeController.class)
class GrpcEmployeeControllerTest {

    @MockitoBean
    private GrpcClientGreeterService greeterClient;
    @MockitoBean
    private GrpcEmployeeClientService grpcEmployeeClientService;

    private MockMvc mockMvc;

    @Autowired
    GrpcEmployeeController grpcEmployeeController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(grpcEmployeeController).build();
    }

    @Test
    void testGetEmployerJobs() throws Exception {
        GrpcEmployerJobDtoList dto = new GrpcEmployerJobDtoList();
        dto.setEmployerJobDtos(List.of(
                new GrpcEmployerJobDto(1, 12345, List.of(101, 102, 103)),
                new GrpcEmployerJobDto(2, 67890, List.of(201, 202, 203))
        ));

        Mockito.when(grpcEmployeeClientService.getEmployerJobInfo(Mockito.anyList())).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/employer-jobs")
                        .param("employeeIds", "3,5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerJobDtos[0].employerId").value(12345))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerJobDtos[0].jobIds[0]").value(101))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerJobDtos[0].jobIds[1]").value(102))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerJobDtos[0].jobIds[2]").value(103))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerJobDtos[1].employerId").value(67890))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerJobDtos[1].jobIds[0]").value(201))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerJobDtos[1].jobIds[1]").value(202))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerJobDtos[1].jobIds[2]").value(203));
    }



    @Test
    void testGreetEndpoint() throws Exception {
        // Mock response from Greeter gRPC service
        Mockito.when(greeterClient.sayHello(Mockito.anyString())).thenReturn("Hello, Copilot!");

        // Perform GET request to /greet with a name parameter
        mockMvc.perform(MockMvcRequestBuilders.get("/greet")
                        .param("name", "Copilot")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Hello, Copilot!"));
    }
}