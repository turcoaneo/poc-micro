package com.poc.microservices.employer.app.controller;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.model.dto.EMGenericResponseDTO;
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


@WebMvcTest(controllers = EMEmployerController.class)
class EMEmployerControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private EMEmployerController EMEmployerController;

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
                .standaloneSetup(EMEmployerController)
                .build();
        System.setProperty("SECRET_KEY", "someUsefulLargeEnoughSecretKeyToBeAtLeast256Bits");
    }

    @Test
    void testPatchAssignEmployeeFromJson() throws Exception {
        String jsonContent = Files.readString(Path.of("src/test/resources/employee_patch_dto.json"));

        // You may adjust the mock behavior depending on what your controller calls
        Mockito.when(employerService.assignEmployeeToJobs(Mockito.any()))
                .thenReturn(new EMGenericResponseDTO(100L, "Added employee"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/employers/employer/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(100L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Added employee"));
    }

    @Test
    void testCreateEmployerFromJson() throws Exception {
        String jsonContent = Files.readString(Path.of("src/test/resources/employer_dto.json"));


        Mockito.when(employerService.createEmployer(Mockito.any(EmployerDTO.class))).thenReturn(0L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/employers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)) // Inject external JSON file
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Employer successfully created"));
    }

    @Test
    void testCreateEmployer() throws Exception {
        Mockito.when(employerService.createEmployer(Mockito.any(EmployerDTO.class))).thenReturn(0L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/employers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "name": "TestCorp"
                                    }
                                """))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(0L));
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