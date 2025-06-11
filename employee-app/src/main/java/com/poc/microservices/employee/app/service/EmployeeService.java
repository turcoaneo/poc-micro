package com.poc.microservices.employee.app.service;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.EmployeeJobEmployer;
import com.poc.microservices.employee.app.model.Employer;
import com.poc.microservices.employee.app.model.Job;
import com.poc.microservices.employee.app.model.dto.EmployeeDTO;
import com.poc.microservices.employee.app.repository.EmployeeJobEmployerRepository;
import com.poc.microservices.employee.app.repository.EmployeeRepository;
import com.poc.microservices.employee.app.repository.EmployerRepository;
import com.poc.microservices.employee.app.repository.JobRepository;
import com.poc.microservices.employee.app.service.util.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;
    private final EmployeeJobEmployerRepository employeeJobEmployerRepository;

    public EmployeeDTO createEmployee(EmployeeDTO dto) {
        Employee employee = employeeMapper.toEntity(dto);
        Set<EmployeeJobEmployer> jobEmployers = new HashSet<>();
        dto.getEmployers().forEach(employerDTO -> {
            Employer employer = new Employer(null, employerDTO.getName(), new HashSet<>());
            employerRepository.save(employer);
            employerDTO.getJobs().forEach(jobDTO -> {
                Job job = jobRepository.save(new Job(null, jobDTO.getTitle(), new HashSet<>()));
                jobRepository.save(job);
                EmployeeJobEmployer jobEmployer = new EmployeeJobEmployer(null, employee, job, employer);
                jobEmployers.add(jobEmployer);
            });
        });
        employee.setJobEmployers(new HashSet<>());
        Employee saved = employeeRepository.save(employee);

        employeeJobEmployerRepository.saveAll(jobEmployers);
        saved = employeeRepository.findById(saved.getId()).orElse(null);
        if (saved == null) {
            throw new RuntimeException("Message in a bottle...neck. Could not save employee!");
        }
        return employeeMapper.toDTO(saved);
    }

    public Optional<EmployeeDTO> getEmployeeById(Long id) {
        return employeeRepository.findById(id).map(employeeMapper::toDTO);
    }

    public List<EmployeeDTO> getEmployeesByName(String name) {
        return employeeRepository.findByName(name).stream()
                .map(employeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<EmployeeDTO> getEmployeesByJobId(Long jobId) {
        return employeeJobEmployerRepository.findEmployeesByJobId(jobId).stream()
                .map(employeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<EmployeeDTO> getEmployeesByEmployerId(Long employerId) {
        return employeeJobEmployerRepository.findEmployeesByEmployerId(employerId).stream()
                .map(employeeMapper::toDTO)
                .collect(Collectors.toList());
    }


}
