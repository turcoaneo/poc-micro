package com.poc.microservices.main.app.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LambdaController.class)
public class LambdaControllerTest {

    @MockitoBean
    private RestTemplate restTemplate;

    private MockMvc mockMvc;

    @Autowired
    LambdaController lambdaController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(lambdaController).build();
    }

    @Test
    public void testGetTimeFromLambda() throws Exception {
        // Mocking Lambda response
        String mockResponse = "{\"time\": \"2025-07-31 17:20:14\"}";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/get-time"))
            .andExpect(status().isOk())
            .andExpect(content().json(mockResponse));
    }
}