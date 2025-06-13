package com.poc.microservices.main.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.microservices.main.app.feign.MASGatewayClient;
import com.poc.microservices.main.app.model.dto.MASEmployeeDTO;
import com.poc.microservices.main.app.model.dto.MASEmployerDTO;
import com.poc.microservices.main.app.model.dto.MASEmployerEmployeeAssignmentPatchDTO;
import com.poc.microservices.main.app.model.dto.MASGenericResponseDTO;
import com.poc.microservices.main.app.model.dto.MASJobDTO;
import com.poc.microservices.main.app.model.dto.UserDTO;
import com.poc.microservices.main.app.util.TestMASHelper;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.List;

@WebMvcTest(MASGatewayController.class)
@ExtendWith(SpringExtension.class)
public class MASGatewayControllerTest {

    @Spy
    TestMASHelper testMASHelper;

    private MockMvc mockMvc;

    @Autowired
    private MASGatewayController masGatewayController;

    @MockitoBean
    private MASGatewayClient masGatewayClient;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(masGatewayController)
                .build();
        System.setProperty("SECRET_KEY", "someUsefulLargeEnoughSecretKeyToBeAtLeast256Bits");
    }

    @Test
    public void testFetchEmployer_Success() throws Exception {
        Mockito.when(masGatewayClient.getTestEmployerRole()).thenReturn(ResponseEntity.ok("Test employer"));

        mockMvc.perform(MockMvcRequestBuilders.get("/mas-gateway/test-employer-role")
                        .header("Authorization", "Bearer " + getValidToken()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Test employer"));
    }

    @Test
    public void testFetchEmployer_Unauthorized() throws Exception {
        Mockito.when(masGatewayClient.getTestEmployerRole())
                .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to fetch employer data"));

        mockMvc.perform(MockMvcRequestBuilders.get("/mas-gateway/test-employer-role")
                        .header("Authorization", "Bearer " + getValidToken()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().string("Failed to fetch employer data"));
    }

    @Test
    public void testFetchUser_Success() throws Exception {
        UserDTO mockUser = new UserDTO("test user", "password", "USER");
        Mockito.when(masGatewayClient.getUser("testuser")).thenReturn(ResponseEntity.ok(mockUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/mas-gateway/fetch-user-role/testuser")
                        .header("Authorization", "Bearer " + getValidToken()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("test user"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.role").value("USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User retrieved successfully"));
    }

    @Test
    public void testFetchUser_Unauthorized() throws Exception {
        Mockito.when(masGatewayClient.getUser("wronguser"))
                .thenThrow(new FeignException.Unauthorized("msg",
                        Request.create(Request.HttpMethod.GET, "", new HashMap<>(), null, null, null),
                        null,
                        new HashMap<>()));

        mockMvc.perform(MockMvcRequestBuilders.get("/mas-gateway/fetch-user-role/wronguser")
                        .header("Authorization", "Bearer " + getValidToken()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void testCreateEmployer_Success() throws Exception {
        MASEmployerDTO employerDTO = new MASEmployerDTO(null, "EmployerName",
                List.of(new MASJobDTO("Job 1", "", 2D)));
        MASGenericResponseDTO response = new MASGenericResponseDTO(1L, "Employer created");

        Mockito.when(masGatewayClient.createEmployer(Mockito.any(MASEmployerDTO.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(response));

        mockMvc.perform(MockMvcRequestBuilders.post("/mas-gateway/create-employer")
                        .header("Authorization", "Bearer " + getValidToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(employerDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Employer created"));
    }

    @Test
    void testCreateEmployee_Unauthorized() throws Exception {
        MASEmployeeDTO dto = new MASEmployeeDTO(null, "John", 20, false);
        MASGenericResponseDTO response = new MASGenericResponseDTO(null, "Employee not created");

        Mockito.when(masGatewayClient.createEmployee(Mockito.any(MASEmployeeDTO.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response));

        mockMvc.perform(MockMvcRequestBuilders.post("/mas-gateway/create-employee")
                        .header("Authorization", "Bearer " + getValidToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Employee not created"));
    }

    @Test
    void testPatchAssignEmployeeThroughGateway() throws Exception {
        MASEmployerEmployeeAssignmentPatchDTO patchDTO = new MASEmployerEmployeeAssignmentPatchDTO();
        patchDTO.setEmployerId(1L);

        MASEmployeeDTO employeeDTO = new MASEmployeeDTO();
        employeeDTO.setId(100L);
        employeeDTO.setName("Alice");
        employeeDTO.setActive(true);
        patchDTO.setEmployee(employeeDTO);
        patchDTO.setJobIds(List.of(10L));

        MASGenericResponseDTO responseDTO = new MASGenericResponseDTO(100L, "Added employee");

        Mockito.when(masGatewayClient.assignEmployeeToJobs(Mockito.any()))
                .thenReturn(ResponseEntity.ok(responseDTO));

        mockMvc.perform(MockMvcRequestBuilders.patch("/mas-gateway/assign-employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(patchDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(100L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Added employee"));
    }

    private String getValidToken() {
        return testMASHelper.getValidToken();
    }
}