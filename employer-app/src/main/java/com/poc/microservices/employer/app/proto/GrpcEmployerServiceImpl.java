package com.poc.microservices.employer.app.proto;


import com.poc.microservices.proto.EmployeeListRequest;
import com.poc.microservices.proto.EmployerJobInfo;
import com.poc.microservices.proto.EmployerJobInfoList;
import com.poc.microservices.proto.EmployerServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
public class GrpcEmployerServiceImpl extends EmployerServiceGrpc.EmployerServiceImplBase {

    @Override
    public void getEmployerJobInfo(EmployeeListRequest request, StreamObserver<EmployerJobInfoList> responseObserver) {
        // Hardcoded response
        EmployerJobInfoList response = EmployerJobInfoList.newBuilder()
                .addAllJobInfos(List.of(EmployerJobInfo.newBuilder().setEmployerId(2).addAllJobIds(List.of(101, 102,
                        103)).build()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}