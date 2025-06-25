package com.poc.microservices.main.app.service;

import com.poc.microservice.main.app.generated.graphql.Employer;
import com.poc.microservices.main.app.graphql.GraphQLEmployerClient;
import com.poc.microservices.main.app.model.dto.graphql.GraphQLMASEmployerDTO;
import com.poc.microservices.main.app.service.util.GraphQLMASMapper;
import org.springframework.stereotype.Service;

@Service
public class GraphQLMASService {

    private final GraphQLEmployerClient gqlClient;
    private final GraphQLMASMapper mapper;

    public GraphQLMASService(GraphQLEmployerClient gqlClient, GraphQLMASMapper mapper) {
        this.gqlClient = gqlClient;
        this.mapper = mapper;
    }

    public GraphQLMASEmployerDTO getEmployerById(Long id) {
        Employer employer = gqlClient.fetchEmployerById(id, 1L);
        return mapper.toDto(employer);
    }
}