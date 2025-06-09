package com.poc.microservices.main.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.microservices.main.app.feign.MASUserClient;
import com.poc.microservices.main.app.model.dto.UserDTO;
import com.poc.microservices.main.app.util.TestMASHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class MASUserControllerTest {

    @Spy
    TestMASHelper testMASHelper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MASUserClient MASUserClient;

    @BeforeEach
    void setUp() {
        System.setProperty("SECRET_KEY", "someUsefulLargeEnoughSecretKeyToBeAtLeast256Bits");
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        UserDTO mockUser = new UserDTO("test user", "password", "USER");
        Mockito.when(MASUserClient.registerUser(Mockito.any(UserDTO.class))).thenReturn(ResponseEntity.ok(mockUser));

        mockMvc.perform(post("/mas-users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test user"))
                .andExpect(jsonPath("$.password").value("password"))
                .andExpect(jsonPath("$.role").value("USER"));


    }

    @Test
    public void testLoginUser_Success() throws Exception {
        String mockToken = getValidToken();
        Mockito.when(MASUserClient.login(Mockito.anyString(), Mockito.anyString())).thenReturn(ResponseEntity.ok(mockToken));

        mockMvc.perform(post("/mas-users/login")
                        .param("username", "test user")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(content().string(mockToken));
    }

    private String getValidToken() {
        return testMASHelper.getValidToken();
    }
}