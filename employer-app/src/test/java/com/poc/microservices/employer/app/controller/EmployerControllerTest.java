package com.poc.microservices.employer.app.controller;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.service.EmployerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;


@WebMvcTest(controllers = EmployerController.class)
class EmployerControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private EmployerController employerController;

    @MockitoBean
    private EmployerService employerService;

    private Employer employer;

    @BeforeEach
    void setUp() {
        employer = new Employer();
        employer.setId(1L);
        employer.setName("TestCorp");
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(employerController)
                .build();
        System.setProperty("SECRET_KEY", "someUsefulLargeEnoughSecretKeyToBeAtLeast256Bits");
    }

    @Test
    void testCreateEmployer() throws Exception {
        Mockito.when(employerService.createEmployer(Mockito.any(Employer.class))).thenReturn(employer);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/employers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "name": "TestCorp"
                                    }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TestCorp"));
    }

    @Test
    void testUpdateEmployer() throws Exception {
        Mockito.when(employerService.updateEmployer(Mockito.eq(1L), Mockito.any(Employer.class))).thenReturn(employer);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/employers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "name": "TestCorp Updated"
                                    }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TestCorp"));
    }

    @Test
    void testGetJobsByEmployerId() throws Exception {
        Job job = new Job();
        job.setTitle("Engineer");

        Mockito.when(employerService.getJobsByEmployerId(1L)).thenReturn(List.of(job));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/employers/1/jobs"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }

    @Test
    void testGetEmployeesByEmployerId() throws Exception {
        Employee emp = new Employee();
        emp.setName("Alice");

        Mockito.when(employerService.getEmployeesByEmployerId(1L)).thenReturn(List.of(emp));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/employers/1/employees"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }

}