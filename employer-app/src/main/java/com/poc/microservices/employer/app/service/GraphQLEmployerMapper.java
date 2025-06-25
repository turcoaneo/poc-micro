package com.poc.microservices.employer.app.service;

import com.poc.microservices.employer.app.graphql.GraphQLEmployeeRecord;
import com.poc.microservices.employer.app.graphql.GraphQLEmployerRecord;
import com.poc.microservices.employer.app.graphql.GraphQLJobRecord;
import com.poc.microservices.employer.app.model.Employee;
import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import com.poc.microservices.employer.app.model.dto.EMJobWorkingHoursDTO;
import com.poc.microservices.employer.app.model.dto.EMWorkingHoursResponseDTO;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
@NoArgsConstructor
public class GraphQLEmployerMapper {

//    @Getter private static boolean schemaMappingSkipped = false;

    public GraphQLEmployerRecord toGraphQLRecord(Employer employer, EMWorkingHoursResponseDTO hoursDTO) {
//        schemaMappingSkipped = true;
        Set<EMJobWorkingHoursDTO> hours = hoursDTO != null
                ? hoursDTO.getJobWorkingHoursDTOS()
                : Collections.emptySet();

        return new GraphQLEmployerRecord(
                employer.getEmployerId(),
                employer.getName(),
                employer.getJobs().stream()
                        .map(job -> toGraphQLRecord(job, hours))
                        .toList()
        );
    }

    private GraphQLJobRecord toGraphQLRecord(Job job, Set<EMJobWorkingHoursDTO> hours) {
        return new GraphQLJobRecord(
                job.getJobId(),
                job.getTitle(),
                job.getEmployees().stream()
                        .map(emp -> {
                            Integer empHours = hours.stream()
                                    .filter(h -> h.getJobId().equals(job.getJobId()))
                                    .map(EMJobWorkingHoursDTO::getWorkingHours)
                                    .findFirst()
                                    .orElse(null);
                            return new GraphQLEmployeeRecord(emp.getEmployeeId(), emp.getName(), empHours);
                        }).toList()
        );
    }

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
        return new GraphQLEmployeeRecord(employee.getEmployeeId(), employee.getName(), 0);
    }

}