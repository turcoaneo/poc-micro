package com.poc.microservices.employee.app.proto;

import com.poc.microservices.employee.app.proto.util.GrpcSslContextHelper;
import com.poc.microservices.proto.GreeterGrpc;
import com.poc.microservices.proto.HelloRequest;
import com.poc.microservices.proto.HelloResponse;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GrpcClientGreeterService {

    private final GreeterGrpc.GreeterBlockingStub greeterStub;

    public GrpcClientGreeterService(@Value("${em.grpc.hostname}") String hostname) throws Exception {
        SslContext sslContext = GrpcSslContextHelper.createSslContext();

        ManagedChannel channel = NettyChannelBuilder.forAddress(hostname, 9093)
                .sslContext(sslContext)
                .build();

        greeterStub = GreeterGrpc.newBlockingStub(channel);
    }

    public String sayHello(String name) {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloResponse response = greeterStub.sayHello(request);
        return response.getMessage();
    }
}