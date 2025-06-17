package com.poc.microservices.employee.app.proto.util;


import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDto;
import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDtoList;
import com.poc.microservices.proto.EmployerJobInfo;
import com.poc.microservices.proto.EmployerJobInfoList;

public class GrpcEmployerJobMapper {
    public static GrpcEmployerJobDtoList mapToDto(EmployerJobInfoList proto) {
        GrpcEmployerJobDtoList result = new GrpcEmployerJobDtoList();
        for (EmployerJobInfo employerJobInfo : proto.getJobInfosList()) {
            result.getEmployerJobDtos().add(new GrpcEmployerJobDto(
                    employerJobInfo.getEmployerId(),
                    employerJobInfo.getJobIdsList() // Converts repeated field from proto to List<Integer>
            ));
        }
        return result;
    }
}