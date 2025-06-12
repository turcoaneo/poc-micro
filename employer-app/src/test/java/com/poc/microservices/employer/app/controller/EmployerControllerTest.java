package com.poc.microservices.employer.app.controller;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.model.dto.EmployeeDTO;
import com.poc.microservices.employer.app.model.dto.EmployerDTO;
import com.poc.microservices.employer.app.model.dto.JobDTO;
import com.poc.microservices.employer.app.service.EmployerService;
import com.poc.microservices.employer.app.service.util.EmployeeMapper;
import com.poc.microservices.employer.app.service.util.EmployerMapper;
import com.poc.microservices.employer.app.service.util.JobMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;


@WebMvcTest(controllers = EmployerController.class)
class EmployerControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private EmployerController employerController;

    @MockitoBean
    private EmployerService employerService;

    @Spy
    private EmployerMapper employerMapper;
    @Spy
    private JobMapper jobMapper;
    @Spy
    private EmployeeMapper employeeMapper;

    private Employer employer;

    @BeforeEach
    void setUp() {
        employer = new Employer();
        employer.setEmployerId(1L);
        employer.setName("TestCorp");
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(employerController)
                .build();
        System.setProperty("SECRET_KEY", "someUsefulLargeEnoughSecretKeyToBeAtLeast256Bits");
    }

    @Test
    void testCreateEmployerFromJson() throws Exception {
        String jsonContent = Files.readString(Path.of("src/test/resources/employer_dto.json"));


        EmployerDTO dto = employerMapper.toDTO(employer);
        Mockito.when(employerService.createEmployer(Mockito.any(EmployerDTO.class))).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/employers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)) // Inject external JSON file
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TestCorp"));
    }

    @Test
    void testCreateEmployer() throws Exception {
        EmployerDTO dto = employerMapper.toDTO(employer);
        Mockito.when(employerService.createEmployer(Mockito.any(EmployerDTO.class))).thenReturn(dto);

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
        EmployerDTO dto = employerMapper.toDTO(employer);
        Mockito.when(employerService.updateEmployer(Mockito.any(EmployerDTO.class))).thenReturn(dto);

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

        JobDTO dto = jobMapper.toDTO(job);
        Mockito.when(employerService.getJobsByEmployerId(1L)).thenReturn(Set.of(dto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/employers/1/jobs"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }

    @Test
    void testGetEmployeesByEmployerId() throws Exception {
        Employee emp = new Employee();
        emp.setName("Alice");

        EmployeeDTO dto = employeeMapper.toDTO(emp);
        Mockito.when(employerService.getEmployeesByEmployerId(1L)).thenReturn(Set.of(dto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/employers/1/employees"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
    }

}