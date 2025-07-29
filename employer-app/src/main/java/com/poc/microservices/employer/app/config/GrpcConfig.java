package com.poc.microservices.employer.app.config;

import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Objects;
import java.util.function.Consumer;

@Configuration
public class GrpcConfig {
    private static final Logger logger = LoggerFactory.getLogger(GrpcConfig.class);
    private static final String jksKeyName = "JKS_KEY";
    private final static String DEFAULT_PASSWORD = System.getenv(jksKeyName) != null ? System.getenv(jksKeyName) :
            System.getProperty(jksKeyName, "jks_pwd"); // default for test contexts

    @Value("${jks.server.filePath}")
    private String filePath;

    @Value("${jks.server.truststore}")
    private String truststore;

    @Value("${grpc.tlsEnabled}")
    private boolean isTlsEnabled;

    @Bean
    public GrpcServerConfigurer tlsConfigurer() throws Exception {
        String folder = "em-server/";
        String keystoreFile = folder + filePath;
        String truststoreFile = folder + truststore;
        char[] storePass = DEFAULT_PASSWORD.toCharArray();

        // Load server keystore (JKS) containing private key and certificate
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (InputStream ksInput = getClass().getClassLoader().getResourceAsStream(keystoreFile)) {
            keyStore.load(ksInput, storePass);
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, storePass);

        // Load truststore (JKS) containing trusted certificates
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (InputStream tsInput = getClass().getClassLoader().getResourceAsStream(truststoreFile)) {
            trustStore.load(tsInput, storePass);
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // Build the Netty SslContext using the KeyManagerFactory and TrustManagerFactory
        SslContext sslContext = GrpcSslContexts.configure(SslContextBuilder.forServer(kmf))
                .trustManager(tmf)
                .build();

        // Return a GrpcServerConfigurer that applies the SSL configuration.
        //noinspection NullableProblems
        return new GrpcServerConfigurer() {
            public void configure(ServerBuilder<?> serverBuilder) {
                if (serverBuilder instanceof NettyServerBuilder && isTlsEnabled) {
                    logger.warn("TLS is not enabled this time");
                    ((NettyServerBuilder) serverBuilder).sslContext(sslContext);
                }
            }

            @Override
            public void accept(ServerBuilder<?> serverBuilder) {
                // Delegate to configure(serverBuilder)
                configure(serverBuilder);
            }

            @Override
            public GrpcServerConfigurer andThen(Consumer<? super ServerBuilder<?>> after) {
                Objects.requireNonNull(after);
                // Chain this configurer with another Consumer.
                return serverBuilder -> {
                    // Apply the SSL configuration first.
                    accept(serverBuilder);
                    // Then, apply the additional configuration.
                    after.accept(serverBuilder);
                };
            }
        };
    }
}