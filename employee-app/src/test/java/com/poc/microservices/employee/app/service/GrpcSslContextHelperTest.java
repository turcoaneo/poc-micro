package com.poc.microservices.employee.app.service;

import com.poc.microservices.employee.app.proto.util.GrpcSslContextHelper;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GrpcSslContextHelperTest {

    @Test
    void testCreateSslContext() throws Exception {
        System.setProperty("JKS_KEY", "changeit");
        SslContext sslContext = GrpcSslContextHelper.createSslContext();
        Assertions.assertNotNull(sslContext, "SSL Context should be created successfully.");
        Assertions.assertFalse(sslContext.cipherSuites().isEmpty());
    }

    @Test
    void testMissingKeyStoreFails() {
        System.setProperty("JKS_KEY", "");
        Assertions.assertThrows(Exception.class, GrpcSslContextHelper::createSslContext);
    }
}