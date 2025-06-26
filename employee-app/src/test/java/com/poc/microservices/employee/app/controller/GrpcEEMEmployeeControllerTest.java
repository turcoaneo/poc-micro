package com.poc.microservices.employee.app.controller;

import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDto;
import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDtoList;
import com.poc.microservices.employee.app.proto.GrpcClientGreeterService;
import com.poc.microservices.employee.app.proto.GrpcEmployeeClientService;
import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.List;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(GrpcEmployeeController.class)
class GrpcEEMEmployeeControllerTest {

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
        GrpcEmployerJobDtoList dto = getGrpcEmployerJobDtoList();

        Mockito.when(grpcEmployeeClientService.getEmployerJobInfo(Mockito.anyList())).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/employer-jobs")
                        .param("employeeIds", "3,5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerJobDtos[0].employerId").value(12345))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerJobDtos[0].employerName").value("Employer 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerJobDtos[0].jobIdToTitle", Matchers.hasEntry("101", "Job 1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerJobDtos[1].employerId").value(67890))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerJobDtos[1].employeeId").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employerJobDtos[1].jobIdToTitle", Matchers.hasEntry("201", "Job 1")));
    }

    private static GrpcEmployerJobDtoList getGrpcEmployerJobDtoList() {
        GrpcEmployerJobDtoList dto = new GrpcEmployerJobDtoList();

        HashMap<Long, String> jobIdToTitle1 = new HashMap<>();
        jobIdToTitle1.put(101L, "Job 1");
        jobIdToTitle1.put(102L, "Job 2");

        HashMap<Long, String> jobIdToTitle2 = new HashMap<>();
        jobIdToTitle2.put(201L, "Job 1");
        dto.setEmployerJobDtos(List.of(
                new GrpcEmployerJobDto(1L, "Employee 1", 12345L, "Employer 1", jobIdToTitle1),
                new GrpcEmployerJobDto(2L, "Employee 2", 67890L, "Employer 2", jobIdToTitle2)
        ));
        return dto;
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