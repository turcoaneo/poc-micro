package com.poc.microservices.employee.app.service;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.EmployeeJobEmployer;
import com.poc.microservices.employee.app.model.Employer;
import com.poc.microservices.employee.app.model.Job;
import com.poc.microservices.employee.app.model.dto.EEMGenericResponseDTO;
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
import java.util.Map;
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

    @Transactional
    public EEMGenericResponseDTO reconcileEmployee(GrpcEmployerJobDto dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Set<EmployeeJobEmployer> existingMappings = employee.getJobEmployers();

        // Fetch or create Employer
        Employer employer = getOrSaveEmployer(dto, existingMappings);
        employer.setName(dto.getEmployerName());

        Set<EmployeeJobEmployer> newMappings = new HashSet<>();
        for (Map.Entry<Long, String> entry : dto.getJobIdToTitle().entrySet()) {
            Long jobId = entry.getKey();
            String title = entry.getValue();

            // Fetch or create Job
            Job job = getOrSaveJob(existingMappings, jobId, title);
            job.setTitle(title);

            // Check if mapping already exists
            populateNewMappings(dto, existingMappings, jobId, employee, job, employer, newMappings);
        }

        employeeJobEmployerRepository.saveAll(newMappings);
        employeeRepository.save(employee);

        return new EEMGenericResponseDTO(employee.getEmployeeId(), "Employee reconciliation successful"
        );
    }

    private static void populateNewMappings(GrpcEmployerJobDto dto, Set<EmployeeJobEmployer> existingMappings, Long jobId, Employee employee, Job job, Employer employer, Set<EmployeeJobEmployer> newMappings) {
        boolean mappingExists = existingMappings.stream()
                .anyMatch(m -> m.getJob().getLocalJobId() != null && m.getJob().getLocalJobId().equals(jobId)
                        && m.getEmployer().getLocalEmployerId().equals(dto.getEmployerId()));

        if (!mappingExists) {
            EmployeeJobEmployer mapping = new EmployeeJobEmployer(null, employee, job, employer);

            // Bidirectional links (needed only on first patch / new mapping)
            employee.getJobEmployers().add(mapping);
            employer.getEmployeesJobs().add(mapping);
            job.getJobEmployees().add(mapping);
            newMappings.add(mapping);
        }
    }

    private Job getOrSaveJob(Set<EmployeeJobEmployer> existingMappings, Long jobId, String title) {
        return existingMappings.stream()
                .map(EmployeeJobEmployer::getJob)
                .filter(j -> j.getLocalJobId() != null && j.getLocalJobId().equals(jobId))
                .findFirst()
                .orElseGet(() -> {
                    Job newJob = new Job(null, jobId, title, new HashSet<>());
                    jobRepository.save(newJob);
                    return newJob;
                });
    }

    private Employer getOrSaveEmployer(GrpcEmployerJobDto dto, Set<EmployeeJobEmployer> existingMappings) {
        return existingMappings.stream()
                .map(EmployeeJobEmployer::getEmployer)
                .filter(e -> e.getLocalEmployerId().equals(dto.getEmployerId()))
                .findFirst()
                .orElseGet(() -> {
                    Employer newEmp = new Employer(null, dto.getEmployerId(), dto.getEmployerName(), new HashSet<>());
                    employerRepository.save(newEmp);
                    return newEmp;
                });
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
}
