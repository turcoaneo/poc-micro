package com.poc.microservices.employee.app.service;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.EmployeeJobEmployer;
import com.poc.microservices.employee.app.model.Employer;
import com.poc.microservices.employee.app.model.Job;
import com.poc.microservices.employee.app.model.dto.EmployeeDTO;
import com.poc.microservices.employee.app.model.dto.EmployerDTO;
import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDto;
import com.poc.microservices.employee.app.repository.EmployeeJobEmployerRepository;
import com.poc.microservices.employee.app.repository.EmployeeRepository;
import com.poc.microservices.employee.app.repository.EmployerRepository;
import com.poc.microservices.employee.app.repository.JobRepository;
import com.poc.microservices.employee.app.service.util.EmployeeMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    public Long createEmployee(EmployeeDTO dto) {
        Employee employee = employeeMapper.toEntity(dto);
        Set<EmployeeJobEmployer> jobEmployers = getEmployeeJobEmployers(dto, employee);
        employee.setJobEmployers(new HashSet<>());
        Employee saved = employeeRepository.save(employee);
        Long employeeId = saved.getEmployeeId();

        employeeJobEmployerRepository.saveAll(jobEmployers);
        employeeJobEmployerRepository.flush();
        return employeeId;
    }

    @Transactional
    public void updateEmployee(GrpcEmployerJobDto dto) {
        Employee employee = employeeRepository.findById((long) dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Set<EmployeeJobEmployer> existingMappings = employee.getJobEmployers();

        // Fetch or create employer
        Employer employer = existingMappings.stream()
                .map(EmployeeJobEmployer::getEmployer)
                .filter(e -> e.getEmployerId().equals((long) dto.getEmployerId()))
                .findFirst()
                .orElseGet(() -> employerRepository.save(new Employer(null, (long) dto.getEmployerId(), "New Employer", new HashSet<>())));

        employer.setName("Updated Employer Name"); // Patch if needed

        Set<EmployeeJobEmployer> updatedMappings = dto.getJobIds().stream()
                .map(jobId -> {
                    Job job = existingMappings.stream()
                            .map(EmployeeJobEmployer::getJob)
                            .filter(j -> j.getJobId().equals((long) jobId))
                            .findFirst()
                            .orElseGet(() -> jobRepository.save(new Job(null, (long) jobId, "New Job", new HashSet<>())));

                    job.setTitle("Updated Job Title"); // Patch if needed

                    return new EmployeeJobEmployer(null, employee, job, employer);
                }).collect(Collectors.toSet());

        employee.setJobEmployers(updatedMappings);
        employeeRepository.save(employee);
        employeeJobEmployerRepository.saveAll(updatedMappings);
    }

    private Set<EmployeeJobEmployer> getEmployeeJobEmployers(EmployeeDTO dto, Employee employee) {
        List<EmployerDTO> employers = dto.getEmployers();
        if (CollectionUtils.isEmpty(employers)) return new HashSet<>();
        Set<EmployeeJobEmployer> jobEmployers = new HashSet<>();
        employers.forEach(employerDTO -> {
            Employer employer = new Employer(null, dto.getId(), employerDTO.getName(), new HashSet<>());
            employerRepository.save(employer);
            employerDTO.getJobs().forEach(jobDTO -> {
                Job job = jobRepository.save(new Job(null, jobDTO.getId(), jobDTO.getTitle(), new HashSet<>()));
                jobRepository.save(job);
                EmployeeJobEmployer jobEmployer = new EmployeeJobEmployer(null, employee, job, employer);
                jobEmployers.add(jobEmployer);
            });
        });
        return jobEmployers;
    }

    public EmployeeDTO getEmployeeDTOById(Long employeeId) {
        Employee saved = employeeRepository.findById(employeeId).orElse(null);
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


    public void deleteEmployee(Long employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        optionalEmployee.ifPresent(employee -> {
            Set<Long> jobIds = employee.getJobEmployers().stream().map(EmployeeJobEmployer::getJob).map(Job::getJobId)
                    .collect(Collectors.toSet());
            Set<Long> employerIds = employee.getJobEmployers().stream().map(EmployeeJobEmployer::getEmployer)
                    .map(Employer::getEmployerId).collect(Collectors.toSet());
            employeeRepository.deleteById(employeeId);
            employeeRepository.flush();
            jobRepository.deleteByIds(jobIds);
            employerRepository.deleteByIds(employerIds);
        });
    }
}
