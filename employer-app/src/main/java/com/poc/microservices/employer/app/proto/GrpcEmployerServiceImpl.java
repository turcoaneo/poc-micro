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
                                .setEmployerId(Math.toIntExact(dto.getEmployerId())) // Fix single ID
                                .addAllJobIds(dto.getJobIds().stream().map(Math::toIntExact).toList()) // Fix list conversion
                                .build())
                        .toList())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}