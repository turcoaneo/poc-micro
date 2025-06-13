package com.poc.microservices.employee.app.service.util;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.EmployeeJobEmployer;
import com.poc.microservices.employee.app.model.Employer;
import com.poc.microservices.employee.app.model.Job;
import com.poc.microservices.employee.app.model.dto.EmployeeDTO;
import com.poc.microservices.employee.app.model.dto.EmployerDTO;
import com.poc.microservices.employee.app.model.dto.JobDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setWorkingHours(dto.getWorkingHours());

        Set<EmployeeJobEmployer> jobEmployers = new HashSet<>();

        if (dto.getEmployers() != null) {
            for (EmployerDTO employerDTO : dto.getEmployers()) {
                Employer employer = new Employer(employerDTO.getId(), employerDTO.getName(), new HashSet<>());

                for (JobDTO jobDTO : employerDTO.getJobs()) {
                    Job job = new Job(jobDTO.getId(), jobDTO.getTitle(), new HashSet<>());
                    jobEmployers.add(new EmployeeJobEmployer(null, employee, job, employer));
                }
            }
        }

        employee.getJobEmployers().addAll(jobEmployers);
        return employee;
    }

    public EmployeeDTO toDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setName(employee.getName());
        dto.setId(employee.getEmployeeId());
        dto.setWorkingHours(employee.getWorkingHours());

        Map<Long, EmployerDTO> employerMap = new HashMap<>();

        for (EmployeeJobEmployer jobEmployer : employee.getJobEmployers()) {
            Employer employer = jobEmployer.getEmployer();
            Job job = jobEmployer.getJob();

            employerMap.computeIfAbsent(employer.getEmployerId(), id ->
                    new EmployerDTO(id, employer.getName(),
                            new ArrayList<>())).getJobs().add(new JobDTO(job.getJobId(), job.getTitle()));
        }

        dto.setEmployers(new ArrayList<>(employerMap.values()));
        return dto;
    }
}