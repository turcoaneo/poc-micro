package com.poc.microservices.employer.app.controller;

import com.poc.microservices.employer.app.feign.EMWorkingHoursClient;
import com.poc.microservices.employer.app.graphql.GraphQLEmployerRecord;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursRequestDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursResponseDTO;
import com.poc.microservices.employer.app.repository.EmployerRepository;
import com.poc.microservices.employer.app.service.GraphQLEmployerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GraphqlEmployerQueryResolver {

    private final EmployerRepository employerRepository;
    private final GraphQLEmployerMapper employerMapper;
    private final EMWorkingHoursClient EMWorkingHoursClient;

    @QueryMapping
    public List<GraphQLEmployerRecord> employers() {
        return employerRepository.findAll().stream()
            .map(employerMapper::toGraphQLRecord)
            .toList();
    }

    @QueryMapping
    public GraphQLEmployerRecord employer(@Argument Long id, @Argument Long employeeId) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        EMWorkingHoursResponseDTO hoursDTO = null;
        if (employeeId != null) {
            EMWorkingHoursRequestDTO dto = new EMWorkingHoursRequestDTO();
            dto.setEmployeeId(employeeId);
            dto.setEmployerId(id);
            hoursDTO = EMWorkingHoursClient.getWorkingHours(dto);
        }

        return employerMapper.toGraphQLRecord(employer, hoursDTO);
    }
}