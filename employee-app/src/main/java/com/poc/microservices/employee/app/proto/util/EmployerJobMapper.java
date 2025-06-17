package com.poc.microservices.employee.app.proto.util;


import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDto;
import com.poc.microservices.proto.EmployerJobInfo;

public class EmployerJobMapper {
    public static GrpcEmployerJobDto mapToDto(EmployerJobInfo proto) {
        return new GrpcEmployerJobDto(
            proto.getEmployerId(),
            proto.getJobIdsList() // Converts repeated field from proto to List<Integer>
        );
    }
}