package com.poc.microservices.main.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.microservices.main.app.model.dto.RestartRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@WebMvcTest(RestartController.class)
@ExtendWith(SpringExtension.class)
public class RestartControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private RestartController restartController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(restartController)
                .build();
    }

    @Test
    void testRestartServiceWithJackson() throws Exception {
        String exePath;
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            exePath = "C:\\Program Files\\Git\\bin\\bash.exe";
        } else {
            exePath = "/usr/bin/bash";
        }

        RestartRequest request = new RestartRequest();
        request.setExePath(exePath);
        request.setFilePath("src/test/resources/restart-service-mock.sh");
        request.setServiceName("EEM2");
        request.setJarName("eem-2.jar");
        request.setPort("8095");
        request.setHealthPath("/eem-2/api/employees/test");

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequest = mapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/restart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Restart triggered for " +
                        "EEM2")));
    }
}