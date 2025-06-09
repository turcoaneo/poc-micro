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
            employer.setJobs(dto.getJobs().stream().map(jobDTO -> this.mapJob(jobDTO, employer)).toList());
        }

        return employer;
    }

    private Job mapJob(JobDTO dto, Employer employer) {
        Job job = new Job();
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setHourRate(dto.getHourRate());

        if (!dto.getEmployees().isEmpty()) {
            employer.setEmployees(dto.getEmployees().stream().map(employeeDTO -> this.mapEmployee(employeeDTO,
                    employer, job)).toList());
        }
        return job;
    }

    private Employee mapEmployee(EmployeeDTO dto, Employer employer, Job job) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setWorkingHours(dto.getWorkingHours());
        employer.getEmployees().add(employee);
        employee.setEmployer(employer);
        employee.getAssignedJobs().add(job);
        job.getAssignedEmployees().add(employee);
        return employee;
    }
}