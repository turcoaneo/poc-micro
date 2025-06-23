package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.graphql.GraphQLEmployeeRecord;
import com.poc.microservices.employer.app.graphql.GraphQLEmployerRecord;
import com.poc.microservices.employer.app.graphql.GraphQLJobRecord;
import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class GraphQLEmployerMapper {

    public GraphQLEmployerRecord toGraphQLRecord(Employer employer) {
        return new GraphQLEmployerRecord(
            employer.getEmployerId(),
            employer.getName(),
            employer.getJobs().stream().map(this::toGraphQLRecord).toList()
        );
    }

    public GraphQLJobRecord toGraphQLRecord(Job job) {
        return new GraphQLJobRecord(
            job.getJobId(),
            job.getTitle(),
            job.getEmployees().stream().map(this::toGraphQLRecord).toList()
        );
    }

    public GraphQLEmployeeRecord toGraphQLRecord(Employee employee) {
        return new GraphQLEmployeeRecord(employee.getEmployeeId(), employee.getName());
    }
}