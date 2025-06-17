package com.poc.microservices.employer.app.proto;


import com.poc.microservices.proto.EmployeeRequest;
import com.poc.microservices.proto.EmployerJobInfo;
import com.poc.microservices.proto.EmployerServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
public class EmployerServiceImpl extends EmployerServiceGrpc.EmployerServiceImplBase {

    @Override
    public void getEmployerJobInfo(EmployeeRequest request, StreamObserver<EmployerJobInfo> responseObserver) {
        // Hardcoded response
        EmployerJobInfo response = EmployerJobInfo.newBuilder()
                .setEmployerId(12345) // Mocked employer ID
                .addAllJobIds(List.of(101, 102, 103)) // Mocked job IDs
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}