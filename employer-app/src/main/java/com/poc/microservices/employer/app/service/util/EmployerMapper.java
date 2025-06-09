package com.poc.microservices.employer.app.service.util;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.model.dto.EmployeeDTO;
import com.poc.microservices.employer.app.model.dto.EmployerDTO;
import com.poc.microservices.employer.app.model.dto.JobDTO;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class EmployerMapper {

    public Employer toEntity(EmployerDTO dto) {
        Employer employer = new Employer();
        employer.setName(dto.getName());

        if (dto.getJobs() != null) {
            employer.setJobs(dto.getJobs().stream().map(this::mapJob).toList());
        }

        if (dto.getEmployees() != null) {
            employer.setEmployees(dto.getEmployees().stream().map(this::mapEmployee).toList());
        }

        return employer;
    }

    private Job mapJob(JobDTO dto) {
        Job job = new Job();
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setHourRate(dto.getHourRate());
        return job;
    }

    private Employee mapEmployee(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setWorkingHours(dto.getWorkingHours());
        return employee;
    }
}