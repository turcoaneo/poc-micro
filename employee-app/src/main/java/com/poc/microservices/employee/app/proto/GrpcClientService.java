package com.poc.microservices.employee.app.proto;

import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDto;
import com.poc.microservices.employee.app.proto.util.EmployerJobMapper;
import com.poc.microservices.employee.app.proto.util.GrpcSslContextHelper;
import com.poc.microservices.proto.EmployeeRequest;
import com.poc.microservices.proto.EmployerJobInfo;
import com.poc.microservices.proto.EmployerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import org.springframework.stereotype.Service;

@Service
public class GrpcClientService {

    private final EmployerServiceGrpc.EmployerServiceBlockingStub employerStub;

    public GrpcClientService() throws Exception {
        SslContext sslContext = GrpcSslContextHelper.createSslContext();

        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 9093)
                .sslContext(sslContext)
                .build();

        employerStub = EmployerServiceGrpc.newBlockingStub(channel);
    }

    public GrpcEmployerJobDto getEmployerJobInfo(int employeeId) {
        EmployeeRequest request = EmployeeRequest.newBuilder().setEmployeeId(employeeId).build();
        EmployerJobInfo employerJobInfo = employerStub.getEmployerJobInfo(request);
        return EmployerJobMapper.mapToDto(employerJobInfo);
    }
}