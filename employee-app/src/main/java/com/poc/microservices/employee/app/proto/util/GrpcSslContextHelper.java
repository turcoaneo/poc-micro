package com.poc.microservices.employee.app.proto.util;

import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

public class GrpcSslContextHelper {
    private static final String jksKeyName = "JKS_KEY";
    private final static String DEFAULT_PASSWORD = System.getenv(jksKeyName) != null ? System.getenv(jksKeyName) :
            System.getProperty(jksKeyName, "test_pwd"); // default for test contexts

    private static final String defaultFolder = "eem-client/";

    public static SslContext createSslContext(String filePath, String truststore) throws Exception {
        String keyStoreFile = defaultFolder + filePath;
        String trustStoreFile = defaultFolder + truststore;
        char[] password = DEFAULT_PASSWORD.toCharArray();

        // Load keystore containing private key and certificate
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (InputStream ksInput = GrpcSslContextHelper.class.getClassLoader().getResourceAsStream(keyStoreFile)) {
            keyStore.load(ksInput, password);
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, password);

        // Load truststore containing trusted certificates
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (InputStream tsInput = GrpcSslContextHelper.class.getClassLoader().getResourceAsStream(trustStoreFile)) {
            trustStore.load(tsInput, password);
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // Build and return the SSL Context
        return GrpcSslContexts.configure(SslContextBuilder.forClient())
                .keyManager(kmf)
                .trustManager(tmf)
                .build();
    }
}