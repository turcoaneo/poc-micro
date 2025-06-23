package com.poc.microservices.main.app.model.dto.graphql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GraphQLMASEmployeeDTO {
    private Long employeeId;
    private String name;
}