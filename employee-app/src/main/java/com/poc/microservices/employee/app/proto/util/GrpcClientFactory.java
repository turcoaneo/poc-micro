package com.poc.microservices.employee.app.proto.util;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;

public class GrpcClientFactory {

    public static ManagedChannel buildChannel(
            String hostname,
            Integer port,
            boolean isTlsEnabled,
            String filePath,
            String truststore
    ) throws Exception {
        NettyChannelBuilder channelBuilder = NettyChannelBuilder.forAddress(hostname, port);

        if (isTlsEnabled) {
            SslContext sslContext = GrpcSslContextHelper.createSslContext(filePath, truststore);
            channelBuilder.sslContext(sslContext);
        } else {
            channelBuilder.usePlaintext();
        }

        return channelBuilder.build();
    }
}