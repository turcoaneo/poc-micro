package com.poc.microservices.employer.app.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Setter
@Getter
@Component
@RequestScope
public class GraphQLWorkingHoursContext {
    private Long employeeId;
    private Long employerId;
}