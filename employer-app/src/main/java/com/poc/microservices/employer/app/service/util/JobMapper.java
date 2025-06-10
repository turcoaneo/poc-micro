package com.poc.microservices.employer.app.service.util;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.model.dto.EmployeeDTO;
import com.poc.microservices.employer.app.model.dto.JobDTO;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class JobMapper {

    public JobDTO toDTO(Job job) {
        JobDTO dto = new JobDTO();
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setHourRate(job.getHourRate());

        if (job.getAssignedEmployees() != null) {
            dto.setEmployees(job.getAssignedEmployees().stream()
                    .map(this::mapEmployeeToDTO)
                    .toList());
        }

        return dto;
    }

    public Job toEntity(JobDTO dto) {
        Job job = new Job();
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setHourRate(dto.getHourRate());

        if (dto.getEmployees() != null) {
            job.setAssignedEmployees(dto.getEmployees().stream()
                    .map(this::mapEmployeeToEntity)
                    .toList());
        }

        return job;
    }

    private EmployeeDTO mapEmployeeToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setName(employee.getName());
        dto.setWorkingHours(employee.getWorkingHours());
        return dto;
    }

    private Employee mapEmployeeToEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setWorkingHours(dto.getWorkingHours());
        return employee;
    }
}