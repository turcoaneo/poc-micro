package com.poc.microservices.main.app.controller;

import com.poc.microservices.main.app.feign.GatewayClient;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MASGatewayController.class)
@ExtendWith(SpringExtension.class)
public class MASGatewayControllerTest {

    @Spy
    TestMASHelper testMASHelper;

    private MockMvc mockMvc;

    @Autowired
    private MASGatewayController masGatewayController;

    @MockitoBean
    private GatewayClient gatewayClient;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(masGatewayController)
                .build();
        System.setProperty("SECRET_KEY", "someUsefulLargeEnoughSecretKeyToBeAtLeast256Bits");
    }

    @Test
    public void testFetchEmployer_Success() throws Exception {
        Mockito.when(gatewayClient.getEmployer()).thenReturn(ResponseEntity.ok("Test employer"));

        mockMvc.perform(get("/mas-gateway/fetch-employer")
                        .header("Authorization", "Bearer " + getValidToken()))
                .andExpect(status().isOk())
                .andExpect(content().string("Test employer"));
    }

    @Test
    public void testFetchEmployer_Unauthorized() throws Exception {
        Mockito.when(gatewayClient.getEmployer())
                .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to fetch employer data"));

        mockMvc.perform(get("/mas-gateway/fetch-employer")
                        .header("Authorization", "Bearer " + getValidToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Failed to fetch employer data"));
    }

    @Test
    public void testFetchUser_Success() throws Exception {
        UserDTO mockUser = new UserDTO("test user", "password", "USER");
        Mockito.when(gatewayClient.getUser("testuser")).thenReturn(ResponseEntity.ok(mockUser));

        mockMvc.perform(get("/mas-gateway/fetch-user/testuser")
                        .header("Authorization", "Bearer " + getValidToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("test user"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.message").value("User retrieved successfully"));
    }

    @Test
    public void testFetchUser_Unauthorized() throws Exception {
        Mockito.when(gatewayClient.getUser("wronguser"))
                .thenThrow(new FeignException.Unauthorized("msg",
                        Request.create(Request.HttpMethod.GET, "", new HashMap<>(), null, null, null),
                        null,
                        new HashMap<>()));

        mockMvc.perform(get("/mas-gateway/fetch-user/wronguser")
                        .header("Authorization", "Bearer " + getValidToken()))
                .andExpect(status().isUnauthorized());
    }

    private String getValidToken() {
        return testMASHelper.getValidToken();
    }
}