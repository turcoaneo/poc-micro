package com.poc.microservices.main.app.service;

import com.poc.microservice.main.app.generated.graphql.Employee;
import com.poc.microservice.main.app.generated.graphql.Employer;
import com.poc.microservice.main.app.generated.graphql.Job;
import com.poc.microservices.main.app.model.dto.graphql.GraphQLMASEmployeeDTO;
import com.poc.microservices.main.app.model.dto.graphql.GraphQLMASEmployerDTO;
import com.poc.microservices.main.app.model.dto.graphql.GraphQLMASJobDTO;
import com.poc.microservices.main.app.service.util.GraphQLMASMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class GraphQLMASMapperTest {

    private final GraphQLMASMapper mapper = new GraphQLMASMapper();

    @Test
    void shouldMapEmployerToDto() {
        // given
        Employee e = new Employee();
        e.setEmployeeId(777L);
        e.setName("Neo");

        Job job = new Job();
        job.setJobId(101L);
        job.setTitle("Architect");
        job.setEmployees(List.of(e));

        Employer employer = new Employer();
        employer.setEmployerId(42L);
        employer.setName("Zion Ltd.");
        employer.setJobs(List.of(job));

        // when
        GraphQLMASEmployerDTO dto = mapper.toDto(employer);

        // then
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(42L, dto.getId());
        Assertions.assertEquals("Zion Ltd.", dto.getName());
        Assertions.assertEquals(1, dto.getJobs().size());

        GraphQLMASJobDTO jobDto = dto.getJobs().getFirst();
        Assertions.assertEquals(101L, jobDto.getJobId());
        Assertions.assertEquals("Architect", jobDto.getTitle());
        Assertions.assertEquals(1, jobDto.getEmployees().size());

        GraphQLMASEmployeeDTO empDto = jobDto.getEmployees().getFirst();
        Assertions.assertEquals(777L, empDto.getEmployeeId());
        Assertions.assertEquals("Neo", empDto.getName());
    }
}