package com.poc.microservices.employee.app.controller;

import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDto;
import com.poc.microservices.employee.app.proto.GrpcClientGreeterService;
import com.poc.microservices.employee.app.proto.GrpcClientService;
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
    private GrpcClientService grpcClientService;

    private MockMvc mockMvc;

    @Autowired
    GrpcEmployeeController grpcEmployeeController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(grpcEmployeeController).build();
    }

    @Test
    void testGreeter() throws Exception {
        GrpcEmployerJobDto dto = new GrpcEmployerJobDto(12345, List.of(101, 102, 103));

        Mockito.when(grpcClientService.getEmployerJobInfo(Mockito.anyInt())).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/employee/3/employer-jobs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerId").value(12345))
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobIds[0]").value(101))
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobIds[1]").value(102))
                .andExpect(MockMvcResultMatchers.jsonPath("$.jobIds[2]").value(103));
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