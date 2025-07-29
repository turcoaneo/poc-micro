package com.poc.microservices.employee.app.proto;

import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDtoList;
import com.poc.microservices.employee.app.proto.util.GrpcClientFactory;
import com.poc.microservices.employee.app.proto.util.GrpcEmployerJobMapper;
import com.poc.microservices.proto.EmployeeListRequest;
import com.poc.microservices.proto.EmployerJobInfoList;
import com.poc.microservices.proto.EmployerServiceGrpc;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrpcEmployeeClientService {
    private static final Logger logger = LoggerFactory.getLogger(GrpcEmployeeClientService.class);
    private final EmployerServiceGrpc.EmployerServiceBlockingStub employerStub;

    public GrpcEmployeeClientService(
            @Value("${em.grpc.hostname}") String hostname,
            @Value("${em.grpc.port}") Integer port,
            @Value("${em.grpc.tlsEnabled}") boolean isTlsEnabled,
            @Value("${jks.client.filePath}") String filePath,
            @Value("${jks.client.truststore}") String truststore) throws Exception {
        logger.info("Cron gRPC call using TLS: " + isTlsEnabled + ", Host: " + hostname + ", Port: " + port);

        ManagedChannel channel = GrpcClientFactory.buildChannel(hostname, port, isTlsEnabled, filePath, truststore);
        employerStub = EmployerServiceGrpc.newBlockingStub(channel);
    }

    public GrpcEmployerJobDtoList getEmployerJobInfo(List<Integer> employeeIds) {
        EmployeeListRequest request = EmployeeListRequest.newBuilder()
                .addAllEmployeeIds(employeeIds)
                .build();
        EmployerJobInfoList employerJobInfo = employerStub.getEmployerJobInfo(request);
        return GrpcEmployerJobMapper.mapToDto(employerJobInfo);
    }
}