package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.model.dto.EmployeeJobDto;
import com.poc.microservices.employer.app.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeJobService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeJobService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public List<EmployeeJobDto> getEmployeeJobInfo(List<Long> employeeIds) {
        List<Employee> employees = employeeRepository.findEmployeesByEmployeeIdIn(employeeIds);

        return employees.stream()
                .map(employee -> new EmployeeJobDto(
                        employee.getEmployeeId(),
                        employee.getName(),
                        employee.getJobs().stream().findFirst().map(Job::getEmployer).map(Employer::getEmployerId).orElse(null),
                        employee.getJobs().stream().findFirst().map(Job::getEmployer).map(Employer::getName).orElse(null),
                        employee.getJobs().stream().collect(Collectors.toMap(
                                Job::getJobId,
                                Job::getTitle,
                                (existing, replacement) -> existing
                        ))
                ))
                .toList();
    }
}