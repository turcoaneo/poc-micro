package com.poc.microservices.employer.app.service.util;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.model.dto.EmployeeDTO;
import com.poc.microservices.employer.app.model.dto.EmployerDTO;
import com.poc.microservices.employer.app.model.dto.JobDTO;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class EmployerMapper {

    public Employer toEntity(EmployerDTO dto) {
        Employer employer = new Employer();
        employer.setEmployerId(dto.getId());
        employer.setName(dto.getName());

        Map<String, Employee> employeeMap = new HashMap<>(); // Store existing employees

        if (dto.getJobs() != null) {
            employer.setJobs(dto.getJobs().stream()
                    .map(jobDTO -> this.mapJob(jobDTO, employer, employeeMap))
                    .collect(Collectors.toSet()));
        }

        return employer;
    }

    private Job mapJob(JobDTO dto, Employer employer, Map<String, Employee> employeeMap) {
        Job job = new Job();
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setHourRate(dto.getHourRate());
        job.setEmployer(employer);

        if (!CollectionUtils.isEmpty(dto.getEmployees())) {
            dto.getEmployees().forEach(employeeDTO -> {
                Employee employee = employeeMap.computeIfAbsent(employeeDTO.getName(),
                        name -> this.mapEmployee(employeeDTO));

                employee.getJobs().add(job);
                job.getEmployees().add(employee);
            });
        }

        return job;
    }

    private Employee mapEmployee(EmployeeDTO dto) {
        return new EmployeeMapper().toEntity(dto);
    }

    public EmployerDTO toDTO(Employer employer) {
        EmployerDTO dto = new EmployerDTO();
        dto.setId(employer.getEmployerId());
        dto.setName(employer.getName());

        if (employer.getJobs() != null) {
            dto.setJobs(employer.getJobs().stream()
                    .map(this::mapJobToDTO)
                    .toList());
        }

        return dto;
    }

    private JobDTO mapJobToDTO(Job job) {
        return new JobMapper().toDTO(job);
    }
}