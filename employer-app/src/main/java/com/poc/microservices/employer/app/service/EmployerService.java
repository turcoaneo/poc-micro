package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.dto.EmployeeDTO;
import com.poc.microservices.employer.app.model.dto.EmployerDTO;
import com.poc.microservices.employer.app.model.dto.JobDTO;
import com.poc.microservices.employer.app.repository.EmployerRepository;
import com.poc.microservices.employer.app.service.util.EmployeeMapper;
import com.poc.microservices.employer.app.service.util.EmployerMapper;
import com.poc.microservices.employer.app.service.util.JobMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmployerService {

    private final EmployerRepository employerRepository;
    private final EmployerMapper employerMapper;
    private final JobMapper jobMapper;
    private final EmployeeMapper employeeMapper;

    public Long createEmployer(EmployerDTO employerDTO) {
        Employer employer = employerRepository.save(employerMapper.toEntity(employerDTO));
        return employer.getEmployerId();
    }

    public EmployerDTO updateEmployer(EmployerDTO updatedEmployerDTO) {
        return employerRepository.findById(updatedEmployerDTO.getId())
                .map(existingEmployer -> {
                    existingEmployer.setName(updatedEmployerDTO.getName());
                    Employer employer = employerRepository.save(existingEmployer);
                    return employerMapper.toDTO(employer);
                })
                .orElseThrow(() -> new RuntimeException("Employer not found"));
    }

    public Optional<EmployerDTO> getEmployerByName(String name) {
        Optional<Employer> optionalEmployer = employerRepository.findByName(name);
        return optionalEmployer.map(employerMapper::toDTO).or(() -> Optional.of(new EmployerDTO()));
    }

    public Set<JobDTO> getJobsByEmployerId(Long employerId) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        return employer.getJobs().stream()
                .map(jobMapper::toDTO)
                .collect(Collectors.toSet());
    }

    public Set<EmployeeDTO> getEmployeesByEmployerId(Long employerId) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        return employer.getJobs().stream().flatMap(job -> job.getEmployees().stream())
                .map(employeeMapper::toDTO)
                .collect(Collectors.toSet());
    }

    public void deleteEmployer(Long employerId) {
        employerRepository.deleteById(employerId);
    }
}