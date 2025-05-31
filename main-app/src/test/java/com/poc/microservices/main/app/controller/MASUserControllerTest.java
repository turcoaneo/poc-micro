package com.poc.microservices.main.app.controller;

import com.poc.microservices.main.app.feign.UserClient;
import com.poc.microservices.main.app.model.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class MASUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserClient userClient;

    @BeforeEach
    void setUp() {
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        UserDTO mockUser = new UserDTO("test user", "password", "USER");
        Mockito.when(userClient.registerUser(Mockito.any(UserDTO.class))).thenReturn(ResponseEntity.ok(mockUser));

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
        String mockToken = "fake-jwt-token";
        Mockito.when(userClient.login(Mockito.anyString(), Mockito.anyString())).thenReturn(ResponseEntity.ok(mockToken));

        mockMvc.perform(post("/mas-users/login")
                .param("username", "test user")
                .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(content().string("fake-jwt-token"));
    }
}