package com.poc.microservices.employee.app.proto;

import com.poc.microservices.employee.app.proto.util.GrpcClientFactory;
import com.poc.microservices.proto.GreeterGrpc;
import com.poc.microservices.proto.HelloRequest;
import com.poc.microservices.proto.HelloResponse;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GrpcClientGreeterService {
    private static final Logger logger = LoggerFactory.getLogger(GrpcClientGreeterService.class);

    private GreeterGrpc.GreeterBlockingStub greeterStub;

    public GrpcClientGreeterService(@Value("${em.grpc.hostname}") String hostname,
                                    @Value("${em.grpc.port}") Integer port,
                                    @Value("${em.grpc.tlsEnabled}") boolean isTlsEnabled,
                                    @Value("${jks.client.filePath}") String filePath,
                                    @Value("${jks.client.truststore}") String truststore) throws Exception {
        ManagedChannel channel = GrpcClientFactory.buildChannel(hostname, port, isTlsEnabled, filePath, truststore);
        greeterStub = GreeterGrpc.newBlockingStub(channel);

    }

    public String sayHello(String name) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String token) {
            logger.info("Token is {}", token);
            Metadata metadata = new Metadata();
            metadata.put(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER), "Bearer " + token);
            greeterStub = greeterStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata));
        } else {
            logger.warn("No authentication extracted from spring context holder");
        }
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloResponse response = greeterStub.sayHello(request);
        return response.getMessage();
    }
}