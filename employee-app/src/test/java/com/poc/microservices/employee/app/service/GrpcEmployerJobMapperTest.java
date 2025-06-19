package com.poc.microservices.employee.app.service;

import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDto;
import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDtoList;
import com.poc.microservices.employee.app.proto.util.GrpcEmployerJobMapper;
import com.poc.microservices.proto.EmployerJobInfo;
import com.poc.microservices.proto.EmployerJobInfoList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

class GrpcEmployerJobMapperTest {

    @Test
    void testMapToDto() {
        // Simulated gRPC response (hard-coded proto object)
        EmployerJobInfo protoJob1 = EmployerJobInfo.newBuilder()
                .setEmployeeId(1)
                .setEmployeeName("Employee 1")
                .setEmployerId(12345)
                .setEmployerName("Employer 1")
                .addAllJobIds(List.of(101, 102, 103))
                .addAllJobTitles(List.of("Job 1", "Job 2", "Job 3"))
                .build();

        EmployerJobInfo protoJob2 = EmployerJobInfo.newBuilder()
                .setEmployeeId(2)
                .setEmployeeName("Employee 2")
                .setEmployerId(67890)
                .setEmployerName("Employer 2")
                .addAllJobIds(List.of(201, 202, 203))
                .addAllJobTitles(List.of("Job 1", "Job 2", "Job 3"))
                .build();

        EmployerJobInfoList protoList = EmployerJobInfoList.newBuilder()
                .addAllJobInfos(List.of(protoJob1, protoJob2))
                .build();

        // Execute mapper
        GrpcEmployerJobDtoList dtoList = GrpcEmployerJobMapper.mapToDto(protoList);

        // Validate the mapping results
        Assertions.assertNotNull(dtoList);
        Assertions.assertEquals(2, dtoList.getEmployerJobDtos().size());

        GrpcEmployerJobDto dto1 = dtoList.getEmployerJobDtos().getFirst();
        Assertions.assertEquals(12345, dto1.getEmployerId());
        Assertions.assertEquals("Employer 1", dto1.getEmployerName());
        Assertions.assertEquals(1, dto1.getEmployeeId());
        Assertions.assertEquals(Set.of(101L, 102L, 103L), dto1.getJobIdToTitle().keySet());

        GrpcEmployerJobDto dto2 = dtoList.getEmployerJobDtos().get(1);
        Assertions.assertEquals("Employee 2", dto2.getEmployeeName());
        Assertions.assertEquals(67890, dto2.getEmployerId());
        Assertions.assertEquals(2, dto2.getEmployeeId());
        Assertions.assertEquals(Set.of(201L, 202L, 203L), dto2.getJobIdToTitle().keySet());
    }
}