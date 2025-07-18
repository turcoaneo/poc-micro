package com.poc.microservices.employee.app.service;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.EmployeeJobEmployer;
import com.poc.microservices.employee.app.model.Employer;
import com.poc.microservices.employee.app.model.Job;
import com.poc.microservices.employee.app.model.dto.EEMGenericResponseDTO;
import com.poc.microservices.employee.app.model.dto.EmployeeDTO;
import com.poc.microservices.employee.app.model.dto.EmployeePatchDTO;
import com.poc.microservices.employee.app.model.dto.EmployerDTO;
import com.poc.microservices.employee.app.model.dto.EmployerEmployeeAssignmentPatchDTO;
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

    public List<Integer> findEmployeeIds() {
        return employeeRepository.findAllEmployeeIds()
                .stream()
                .map(Long::intValue)
                .collect(Collectors.toList());
    }

    public void patchEmployeeAssignment(EmployerEmployeeAssignmentPatchDTO dto) {
        Long employerId = dto.getEmployerId();
        EmployeePatchDTO empPatch = dto.getEmployee();
        Map<Long, Integer> jobMap = dto.getJobIdWorkingHoursMap();

        // Load employee
        Employee employee = employeeRepository.findById(empPatch.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Patch name and active
        if (empPatch.getName() != null) employee.setName(empPatch.getName());

        // Update working hours
        Set<EmployeeJobEmployer> assignments = employee.getJobEmployers().stream()
                .filter(eje -> eje.getEmployer().getEmployerId().equals(employerId))
                .collect(Collectors.toSet());

        if (jobMap == null || jobMap.isEmpty()) {
            // Patch all jobs for that employer
            assignments.forEach(eje -> eje.setWorkingHours(eje.getWorkingHours() != null ? eje.getWorkingHours() : 0));
        } else {
            // Patch specific jobs
            assignments.forEach(eje -> {
                if (jobMap.containsKey(eje.getJob().getJobId())) {
                    eje.setWorkingHours(jobMap.get(eje.getJob().getJobId()));
                }
            });
        }

        employeeRepository.saveAndFlush(employee); // cascade will save EJE too
    }

    public Long createEmployee(EmployeeDTO dto) {
        Employee employee = employeeMapper.toEntity(dto);
        Set<EmployeeJobEmployer> jobEmployers = setEmployeeJobEmployers(dto, employee);
        employee.setJobEmployers(new HashSet<>());
        Employee saved = employeeRepository.saveAndFlush(employee);
        Long employeeId = saved.getEmployeeId();

        employeeJobEmployerRepository.saveAllAndFlush(jobEmployers);
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

        employeeJobEmployerRepository.saveAllAndFlush(newMappings);
        employeeRepository.saveAndFlush(employee);

        return new EEMGenericResponseDTO(employee.getEmployeeId(), "Employee reconciliation successful"
        );
    }

    private static void populateNewMappings(GrpcEmployerJobDto dto, Set<EmployeeJobEmployer> existingMappings, Long jobId, Employee employee, Job job, Employer employer, Set<EmployeeJobEmployer> newMappings) {
        boolean mappingExists = existingMappings.stream()
                .anyMatch(m -> m.getJob().getLocalJobId() != null && m.getJob().getLocalJobId().equals(jobId)
                        && m.getEmployer().getLocalEmployerId().equals(dto.getEmployerId()));

        if (!mappingExists) {
            EmployeeJobEmployer mapping = new EmployeeJobEmployer(null, employee, job, employer, 0);

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
                    jobRepository.saveAndFlush(newJob);
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
                    employerRepository.saveAndFlush(newEmp);
                    return newEmp;
                });
    }

    private Set<EmployeeJobEmployer> setEmployeeJobEmployers(EmployeeDTO dto, Employee employee) {
        List<EmployerDTO> employers = dto.getEmployerDTOS();
        if (CollectionUtils.isEmpty(employers)) return new HashSet<>();
        Set<EmployeeJobEmployer> jobEmployers = new HashSet<>();
        employers.forEach(employerDTO -> {
            Employer employer = new Employer(null, employerDTO.getEmployerId(), employerDTO.getName(), new HashSet<>());
            employerRepository.saveAndFlush(employer);
            employerDTO.getJobDTOS().forEach(jobDTO -> {
                Job job = jobRepository.saveAndFlush(new Job(null, jobDTO.getJobId(), jobDTO.getTitle(), new HashSet<>()));
                jobRepository.saveAndFlush(job);
                EmployeeJobEmployer jobEmployer = new EmployeeJobEmployer(null, employee, job, employer,
                        jobDTO.getWorkingHours());
                jobEmployers.add(jobEmployer);
            });
        });
        return jobEmployers;
    }
}
