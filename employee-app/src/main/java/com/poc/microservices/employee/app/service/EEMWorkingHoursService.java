package com.poc.microservices.employee.app.service;

import com.poc.microservices.employee.app.model.dto.JobWorkingHoursDTO;
import com.poc.microservices.employee.app.model.dto.WorkingHoursResponseDTO;
import com.poc.microservices.employee.app.repository.EmployeeJobEmployerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EEMWorkingHoursService {

    private final EmployeeJobEmployerRepository ejeRepository;

    public WorkingHoursResponseDTO getWorkingHours(Long employeeId, Long employerId, List<Long> jobIds) {
        List<JobWorkingHoursDTO> workingHours;

        if (jobIds == null || jobIds.isEmpty()) {
            workingHours = ejeRepository.findWorkingHoursByEmployeeEmployerAndJobs(employeeId, employerId, null);
        } else {
            workingHours = ejeRepository.findWorkingHoursByEmployeeEmployerAndJobs(employeeId, employerId, jobIds);
        }

        WorkingHoursResponseDTO response = new WorkingHoursResponseDTO();
        response.setEmployeeId(employeeId);
        response.setEmployerId(employerId);
        response.setJobWorkingHoursDTOS(new HashSet<>(workingHours));

        return response;
    }
}