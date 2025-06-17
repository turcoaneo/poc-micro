package com.poc.microservices.employee.app.proto;

import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDto;
import com.poc.microservices.employee.app.proto.util.EmployerJobMapper;
import com.poc.microservices.proto.EmployeeRequest;
import com.poc.microservices.proto.EmployerJobInfo;
import com.poc.microservices.proto.EmployerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import org.springframework.stereotype.Service;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

@Service
public class GrpcClientService {

    private final EmployerServiceGrpc.EmployerServiceBlockingStub employerStub;

    public GrpcClientService() throws Exception {
        String folder = "eem-client/";
        char[] storePass = "changeit".toCharArray();

        // Load client keystore (JKS) containing private key and certificate
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (InputStream ksInput = getClass().getClassLoader().getResourceAsStream(folder + "client.jks")) {
            keyStore.load(ksInput, storePass);
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, storePass);

        // Load truststore (JKS) containing trusted server certificate
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (InputStream tsInput = getClass().getClassLoader().getResourceAsStream(folder + "client-truststore.jks")) {
            trustStore.load(tsInput, storePass);
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // Build SSL context for secure connection
        SslContext sslContext = GrpcSslContexts.configure(SslContextBuilder.forClient())
                .keyManager(kmf)
                .trustManager(tmf)
                .build();

        // Create a secured gRPC channel
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