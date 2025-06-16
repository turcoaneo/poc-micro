package com.poc.microservices.employee.app.proto;

import com.poc.microservices.proto.GreeterGrpc;
import com.poc.microservices.proto.HelloRequest;
import com.poc.microservices.proto.HelloResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

@Service
public class GrpcClientService {
    private final GreeterGrpc.GreeterBlockingStub greeterStub;

    public GrpcClientService() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9093)
                .usePlaintext() // Remove this for TLS
                .build();
        greeterStub = GreeterGrpc.newBlockingStub(channel);
    }

    public String sayHello(String name) {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloResponse response = greeterStub.sayHello(request);
        return response.getMessage();
    }
}