package com.poc.microservices.employee.app.service.util;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.EmployeeJobEmployer;
import com.poc.microservices.employee.app.model.Employer;
import com.poc.microservices.employee.app.model.Job;
import com.poc.microservices.employee.app.model.dto.EmployeeDTO;
import com.poc.microservices.employee.app.model.dto.EmployerDTO;
import com.poc.microservices.employee.app.model.dto.JobDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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

        Set<EmployeeJobEmployer> jobEmployers = new HashSet<>();

        if (dto.getEmployerDTOS() != null) {
            for (EmployerDTO employerDTO : dto.getEmployerDTOS()) {
                Employer employer = new Employer(null, employerDTO.getEmployerId(), employerDTO.getName(), new HashSet<>());

                if (CollectionUtils.isEmpty(employerDTO.getJobDTOS())) break;

                for (JobDTO jobDTO : employerDTO.getJobDTOS()) {
                    Job job = new Job(null, jobDTO.getJobId(), jobDTO.getTitle(), new HashSet<>());
                    jobEmployers.add(new EmployeeJobEmployer(null, employee, job, employer, jobDTO.getWorkingHours()));
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

        Map<Long, EmployerDTO> employerMap = new HashMap<>();

        for (EmployeeJobEmployer jobEmployer : employee.getJobEmployers()) {
            Employer employer = jobEmployer.getEmployer();
            Job job = jobEmployer.getJob();

            employerMap.computeIfAbsent(employer.getEmployerId(), id ->
                    new EmployerDTO(id, employer.getName(),
                            new ArrayList<>())).getJobDTOS().add(new JobDTO(job.getJobId(), job.getTitle(), jobEmployer.getWorkingHours()));
        }

        dto.setEmployerDTOS(new ArrayList<>(employerMap.values()));
        return dto;
    }
}