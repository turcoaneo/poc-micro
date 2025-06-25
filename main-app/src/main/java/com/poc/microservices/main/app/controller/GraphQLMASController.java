package com.poc.microservices.main.app.controller;

import com.poc.microservices.main.app.model.dto.graphql.GraphQLMASEmployerDTO;
import com.poc.microservices.main.app.service.GraphQLMASService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/graphql")
public class GraphQLMASController {

    private final GraphQLMASService service;

    public GraphQLMASController(GraphQLMASService service) {
        this.service = service;
    }

    @GetMapping("/employer/{id}")
    public ResponseEntity<GraphQLMASEmployerDTO> getEmployer(@PathVariable Long id) {
        GraphQLMASEmployerDTO dto = service.getEmployerById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/employers")
    public ResponseEntity<List<GraphQLMASEmployerDTO>> getEmployer() {
        List<GraphQLMASEmployerDTO> dto = service.getEmployers();
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }
}