package com.poc.microservices.employer.app.proto;

import com.poc.microservices.proto.GreeterGrpc;
import com.poc.microservices.proto.HelloRequest;
import com.poc.microservices.proto.HelloResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class GreeterService extends GreeterGrpc.GreeterImplBase {
    private static final Logger logger = LoggerFactory.getLogger(GreeterService.class);
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        String name = request.getName();
        logger.info("GRPC Greeter called: {}", name);
        String message = "Hello, " + name + "!";
        HelloResponse response = HelloResponse.newBuilder().setMessage(message).build();
        // Send the response
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}