package com.poc.microservices.employee.app.proto;

import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDtoList;
import com.poc.microservices.employee.app.proto.util.GrpcEmployerJobMapper;
import com.poc.microservices.employee.app.proto.util.GrpcSslContextHelper;
import com.poc.microservices.proto.EmployeeListRequest;
import com.poc.microservices.proto.EmployerJobInfoList;
import com.poc.microservices.proto.EmployerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrpcEmployeeClientService {
    private final EmployerServiceGrpc.EmployerServiceBlockingStub employerStub;

    public GrpcEmployeeClientService(@Value("${em.grpc.hostname}") String hostname) throws Exception {
        SslContext sslContext = GrpcSslContextHelper.createSslContext();

        ManagedChannel channel = NettyChannelBuilder.forAddress(hostname, 9093)
                .sslContext(sslContext)
                .build();

        employerStub = EmployerServiceGrpc.newBlockingStub(channel);
    }

    public GrpcEmployerJobDtoList getEmployerJobInfo(List<Integer> employeeIds) {
        EmployeeListRequest request = EmployeeListRequest.newBuilder().addAllEmployeeIds(employeeIds).build();
        EmployerJobInfoList employerJobInfo = employerStub.getEmployerJobInfo(request);
        return GrpcEmployerJobMapper.mapToDto(employerJobInfo);
    }
}