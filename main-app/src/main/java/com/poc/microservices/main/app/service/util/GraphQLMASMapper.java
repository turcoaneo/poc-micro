package com.poc.microservices.main.app.service.util;

import com.poc.microservice.main.app.generated.graphql.Employee;
import com.poc.microservice.main.app.generated.graphql.Employer;
import com.poc.microservice.main.app.generated.graphql.Job;
import com.poc.microservices.main.app.model.dto.graphql.GraphQLMASEmployeeDTO;
import com.poc.microservices.main.app.model.dto.graphql.GraphQLMASEmployerDTO;
import com.poc.microservices.main.app.model.dto.graphql.GraphQLMASJobDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GraphQLMASMapper {

    public GraphQLMASEmployerDTO toDto(Employer employer) {
        if (employer == null) return null;

        GraphQLMASEmployerDTO dto = new GraphQLMASEmployerDTO();
        dto.setId(employer.getEmployerId());
        dto.setName(employer.getName());

        if (employer.getJobs() != null) {
            List<GraphQLMASJobDTO> jobs = employer.getJobs().stream()
                    .map(this::toDto)
                    .toList();
            dto.setJobs(jobs);
        }

        return dto;
    }

    public GraphQLMASJobDTO toDto(Job job) {
        GraphQLMASJobDTO dto = new GraphQLMASJobDTO();
        dto.setJobId(job.getJobId());
        dto.setTitle(job.getTitle());

        if (job.getEmployees() != null) {
            List<GraphQLMASEmployeeDTO> employees = job.getEmployees().stream()
                    .map(this::toDto)
                    .toList();
            dto.setEmployees(employees); // 👈 field name is `jobs`, but it's a list of employees
        }

        return dto;
    }

    public GraphQLMASEmployeeDTO toDto(Employee employee) {
        GraphQLMASEmployeeDTO dto = new GraphQLMASEmployeeDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setName(employee.getName());
        return dto;
    }
}