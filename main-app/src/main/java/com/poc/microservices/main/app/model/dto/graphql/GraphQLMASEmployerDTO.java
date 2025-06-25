package com.poc.microservices.main.app.model.dto.graphql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GraphQLMASEmployerDTO {
    private Long id = null;
    private String name;
    private List<GraphQLMASJobDTO> jobs;
}