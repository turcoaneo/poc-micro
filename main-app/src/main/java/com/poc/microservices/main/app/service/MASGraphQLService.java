package com.poc.microservices.main.app.service;

import com.poc.microservice.main.app.generated.graphql.Employer;
import com.poc.microservices.main.app.graphql.MASGraphQLEmployerClient;
import com.poc.microservices.main.app.model.dto.graphql.GraphQLMASEmployerDTO;
import com.poc.microservices.main.app.service.util.GraphQLMASMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MASGraphQLService {

    private final MASGraphQLEmployerClient gqlClient;
    private final GraphQLMASMapper mapper;

    public MASGraphQLService(MASGraphQLEmployerClient gqlClient, GraphQLMASMapper mapper) {
        this.gqlClient = gqlClient;
        this.mapper = mapper;
    }

    public GraphQLMASEmployerDTO getEmployerById(Long id) {
        Employer employer = gqlClient.fetchEmployerById(id);
        return mapper.toDto(employer);
    }

    public List<GraphQLMASEmployerDTO> getEmployers() {
        List<Employer> employers = gqlClient.fetchEmployers();
        return employers.stream().map(mapper::toDto).collect(Collectors.toList());
    }
}