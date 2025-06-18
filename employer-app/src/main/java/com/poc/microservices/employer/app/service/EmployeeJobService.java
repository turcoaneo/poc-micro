package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.model.dto.EmployeeJobDto;
import com.poc.microservices.employer.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeJobService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeJobService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeJobDto> getEmployeeJobInfo(List<Long> employeeIds) {
        List<Employee> employees = employeeRepository.findEmployeesByEmployeeIdIn(employeeIds);

        return employees.stream()
                .map(employee -> new EmployeeJobDto(
                        employee.getEmployeeId(),
                        employee.getJobs().stream().findFirst().map(Job::getEmployer).map(Employer::getEmployerId).orElse(null),
                        employee.getJobs().stream().map(Job::getJobId).toList()
                ))
                .toList();
    }
}