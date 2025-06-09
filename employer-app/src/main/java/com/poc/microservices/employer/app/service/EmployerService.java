package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.repository.EmployerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmployerService {

    private final EmployerRepository employerRepository;

    public Employer createEmployer(Employer employer) {
        return employerRepository.save(employer);
    }

    public Employer updateEmployer(Long id, Employer updatedEmployer) {
        return employerRepository.findById(id)
                .map(existingEmployer -> {
                    existingEmployer.setName(updatedEmployer.getName());
                    return employerRepository.save(existingEmployer);
                })
                .orElseThrow(() -> new RuntimeException("Employer not found"));
    }

    public Optional<Employer> getEmployerByName(String name) {
        return employerRepository.findByName(name);
    }

    public List<Job> getJobsByEmployerId(Long employerId) {
        return employerRepository.findById(employerId)
                .map(Employer::getJobs)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
    }

    public List<Employee> getEmployeesByEmployerId(Long employerId) {
        return employerRepository.findById(employerId)
                .map(Employer::getEmployees)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
    }
}