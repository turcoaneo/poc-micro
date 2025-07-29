package com.poc.microservices.employee.app.service;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.EmployeeJobEmployer;
import com.poc.microservices.employee.app.model.Employer;
import com.poc.microservices.employee.app.model.Job;
import com.poc.microservices.employee.app.model.dto.EmployeePatchDTO;
import com.poc.microservices.employee.app.model.dto.EmployerEmployeeAssignmentPatchDTO;
import com.poc.microservices.employee.app.repository.EmployeeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class EmployeePatchServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    // setup: a reusable test employee
    private Employee employee;

    @BeforeEach
    void setup() {
        Employer employer = new Employer();
        employer.setEmployerId(42L);

        Job job = new Job();
        job.setJobId(101L);
        job.setTitle("Analyst");

        EmployeeJobEmployer eje = new EmployeeJobEmployer();
        eje.setEmployer(employer);
        eje.setJob(job);
        eje.setWorkingHours(10);

        employee = new Employee();
        employee.setEmployeeId(777L);
        employee.setName("Ada Lovelace");
        employee.setJobEmployers(Set.of(eje));
        eje.setEmployee(employee);
    }

    @Test
    void shouldReturnAllEmployeeIdsAsIntegers() {
        // Given
        List<Long> mockLongIds = List.of(101L, 102L);
        Mockito.when(employeeRepository.findAllEmployeeIds()).thenReturn(mockLongIds);

        // When
        List<Integer> result = employeeService.findEmployeeIds();

        // Then
        List<Integer> expected = List.of(101, 102);
        Assertions.assertEquals(expected, result);
        Mockito.verify(employeeRepository).findAllEmployeeIds();
    }

    @Test
    void patchAssignment_updatesNameAndWorkingHours() {
        // Given
        Mockito.when(employeeRepository.findById(777L)).thenReturn(Optional.of(employee));

        EmployerEmployeeAssignmentPatchDTO patchDTO = new EmployerEmployeeAssignmentPatchDTO();
        patchDTO.setEmployerId(42L);

        EmployeePatchDTO employeePatch = new EmployeePatchDTO();
        employeePatch.setId(777L);
        employeePatch.setName("Updated Name");
        patchDTO.setEmployee(employeePatch);

        patchDTO.setJobIdWorkingHoursMap(Map.of(101L, 25));

        // When
        employeeService.patchEmployeeAssignment(patchDTO);

        // Then
        Assertions.assertEquals("Updated Name", employee.getName());
        EmployeeJobEmployer eje = employee.getJobEmployers().iterator().next();
        Assertions.assertEquals(25, eje.getWorkingHours());
        Mockito.verify(employeeRepository).saveAndFlush(employee);
    }
}