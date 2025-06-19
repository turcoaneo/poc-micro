package com.poc.microservices.employer.app.proto;


import com.poc.microservices.employer.app.model.dto.EmployeeJobDto;
import com.poc.microservices.employer.app.service.EmployeeJobService;
import com.poc.microservices.proto.EmployeeListRequest;
import com.poc.microservices.proto.EmployerJobInfo;
import com.poc.microservices.proto.EmployerJobInfoList;
import com.poc.microservices.proto.EmployerServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@GrpcService
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GrpcEmployerServiceImpl extends EmployerServiceGrpc.EmployerServiceImplBase {

    private final EmployeeJobService employeeJobService;

    @Override
    public void getEmployerJobInfo(EmployeeListRequest request, StreamObserver<EmployerJobInfoList> responseObserver) {
        List<EmployeeJobDto> employeeJobDtos = employeeJobService.getEmployeeJobInfo(
                request.getEmployeeIdsList().stream().map(Long::valueOf).toList() // Convert List<Integer> to List<Long>
        );

        EmployerJobInfoList response = EmployerJobInfoList.newBuilder()
                .addAllJobInfos(employeeJobDtos.stream()
                        .map(dto -> EmployerJobInfo.newBuilder()
                                .setEmployeeId(Math.toIntExact(dto.getEmployeeId()))
                                .setEmployeeName(dto.getEmployeeName())
                                .setEmployerId(Math.toIntExact(dto.getEmployerId()))
                                .setEmployerName(dto.getEmployerName())
                                .addAllJobIds(dto.getJobIdToTitle().keySet().stream()
                                        .map(Long::intValue).toList())
                                .addAllJobTitles(new ArrayList<>(dto.getJobIdToTitle().values()))
                                .build())
                        .toList())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}