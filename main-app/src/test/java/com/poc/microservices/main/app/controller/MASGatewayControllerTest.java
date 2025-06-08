package com.poc.microservices.main.app.controller;

import com.poc.microservices.main.app.feign.GatewayClient;
import com.poc.microservices.main.app.model.dto.UserDTO;
import feign.FeignException;
import feign.Request;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MASGatewayController.class)
@ExtendWith(SpringExtension.class)
public class MASGatewayControllerTest {

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
        Instant issuedAt = Instant.now();  // Token issued now
        Instant expiration = issuedAt.plus(1, ChronoUnit.HOURS);  // Extend expiration to 1 hour

        return Jwts.builder()
                .claims().subject("USER").and()
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(this.getTestSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getTestSigningKey() {
        return Keys.hmacShaKeyFor("someUsefulLargeEnoughSecretKeyToBeAtLeast256Bits".getBytes());
    }
}