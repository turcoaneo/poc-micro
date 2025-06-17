package com.poc.microservices.employee.app.proto;

import com.poc.microservices.proto.GreeterGrpc;
import com.poc.microservices.proto.HelloRequest;
import com.poc.microservices.proto.HelloResponse;
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

    private final GreeterGrpc.GreeterBlockingStub greeterStub;

    public GrpcClientService() throws Exception {
        String folder = "eem-client/";
        char[] storePass = "changeit".toCharArray();

        // Load the client's keystore (JKS) holding its private key and certificate
        KeyStore clientKeyStore = KeyStore.getInstance("JKS");
        try (InputStream ksInput = getClass().getClassLoader().getResourceAsStream(folder + "server.jks");) {
            clientKeyStore.load(ksInput, storePass);
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientKeyStore, storePass);

        // Load the client's truststore (JKS) that trusts the server certificate
        KeyStore clientTrustStore = KeyStore.getInstance("JKS");
        try (InputStream tsInput = getClass().getClassLoader().getResourceAsStream(folder + "client-truststore.jks")) {
            clientTrustStore.load(tsInput, storePass);
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(clientTrustStore);

        // Build the SslContext for the client using the loaded key and trust stores
        SslContext sslContext = GrpcSslContexts
                .configure(SslContextBuilder.forClient())
                .keyManager(kmf)
                .trustManager(tmf)
                .build();

        // Create a ManagedChannel with the SslContext
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 9093)
                .sslContext(sslContext)
                .build();



        // Create the blocking stub for the Greeter service
        greeterStub = GreeterGrpc.newBlockingStub(channel);
    }

    public String sayHello(String name) {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloResponse response = greeterStub.sayHello(request);
        return response.getMessage();
    }
}