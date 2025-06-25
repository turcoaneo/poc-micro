package com.poc.microservices.main.app.model.dto.graphql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GraphQLMASJobDTO {
    private Long jobId;
    private String title;
    private List<GraphQLMASEmployeeDTO> employees;
}