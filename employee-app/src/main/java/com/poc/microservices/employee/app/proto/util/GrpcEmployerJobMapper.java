package com.poc.microservices.employee.app.proto.util;


import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDto;
import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDtoList;
import com.poc.microservices.proto.EmployerJobInfo;
import com.poc.microservices.proto.EmployerJobInfoList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GrpcEmployerJobMapper {
    public static GrpcEmployerJobDtoList mapToDto(EmployerJobInfoList proto) {
        GrpcEmployerJobDtoList result = new GrpcEmployerJobDtoList();

        for (EmployerJobInfo employerJobInfo : proto.getJobInfosList()) {
            List<Integer> jobIds = employerJobInfo.getJobIdsList();
            List<String> jobTitles = employerJobInfo.getJobTitlesList();

            Map<Long, String> jobMap = new LinkedHashMap<>();
            for (int i = 0; i < Math.min(jobIds.size(), jobTitles.size()); i++) {
                jobMap.put((long) jobIds.get(i), jobTitles.get(i));
            }

            result.getEmployerJobDtos().add(new GrpcEmployerJobDto(
                    (long) employerJobInfo.getEmployeeId(),
                    employerJobInfo.getEmployeeName(),
                    (long) employerJobInfo.getEmployerId(),
                    employerJobInfo.getEmployerName(),
                    jobMap
            ));
        }

        return result;
    }
}