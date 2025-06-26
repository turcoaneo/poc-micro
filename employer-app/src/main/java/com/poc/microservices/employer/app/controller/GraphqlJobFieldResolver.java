package com.poc.microservices.employer.app.controller;

import com.poc.microservices.employer.app.graphql.GraphQLEmployeeRecord;
import com.poc.microservices.employer.app.graphql.GraphQLJobRecord;
import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.GraphQLWorkingHoursContext;
import com.poc.microservices.employer.app.model.dto.EMJobWorkingHoursDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursRequestDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursResponseDTO;
import com.poc.microservices.employer.app.repository.EmployeeRepository;
import com.poc.microservices.employer.app.service.EMWorkingHoursService;
import com.poc.microservices.employer.app.service.util.GraphQLEmployerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GraphqlJobFieldResolver {

    private final EmployeeRepository employeeRepository;
    private final GraphQLEmployerMapper mapper;
    private final EMWorkingHoursService emWorkingHoursService;
    private final GraphQLWorkingHoursContext workingHoursContext;


    @SchemaMapping(typeName = "Job", field = "employees")
    public List<GraphQLEmployeeRecord> resolveEmployees(GraphQLJobRecord job) {
        List<Employee> emEmployees = employeeRepository.findByJobsJobId(job.jobId());
        Long employerId = workingHoursContext.getEmployerId();
        if (employerId != null) {
            EMWorkingHoursRequestDTO requestDTO = new EMWorkingHoursRequestDTO();
            requestDTO.setEmployerId(employerId);
            EMWorkingHoursResponseDTO hoursDTO = emWorkingHoursService.getWorkingHours(requestDTO);
            Set<EMJobWorkingHoursDTO> hours = hoursDTO != null
                    ? hoursDTO.getJobWorkingHoursDTOS()
                    : Collections.emptySet();
            return emEmployees.stream()
                    .map(employee -> mapper.toGraphQLRecord(employee, job.jobId(), hours))
                    .toList();
        } else {
            return emEmployees.stream()
                    .map(mapper::toGraphQLRecord)
                    .toList();
        }
    }
}