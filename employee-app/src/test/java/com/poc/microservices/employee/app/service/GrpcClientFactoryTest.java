package com.poc.microservices.employee.app.service;

import com.poc.microservices.employee.app.proto.util.GrpcClientFactory;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class GrpcClientFactoryTest {

    private static final String HOSTNAME = "localhost";
    private static final int PORT = 9093;

    @Test
    void shouldBuildPlaintextChannelWhenTlsDisabled() throws Exception {
        ManagedChannel channel = GrpcClientFactory.buildChannel(
                HOSTNAME,
                PORT,
                false,
                "",     // No cert needed
                ""      // No truststore needed
        );

        Assertions.assertNotNull(channel);
        Assertions.assertFalse(channel.isShutdown());
        Assertions.assertFalse(channel.isTerminated());
        channel.shutdownNow();channel.shutdown();
        channel.awaitTermination(2, TimeUnit.SECONDS);
    }

    @Test
    void shouldBuildSecureChannelWhenTlsEnabled() throws Exception {
        String filePath = "src/test/resources/client.jks";
        String truststore = "src/test/resources/client-truststore.pem";

        ManagedChannel channel = GrpcClientFactory.buildChannel(
                HOSTNAME,
                PORT,
                true,
                filePath,
                truststore
        );

        Assertions.assertNotNull(channel);
        Assertions.assertFalse(channel.isShutdown());
        Assertions.assertFalse(channel.isTerminated());
        channel.shutdownNow();channel.shutdown();
        channel.awaitTermination(2, TimeUnit.SECONDS);
    }
}