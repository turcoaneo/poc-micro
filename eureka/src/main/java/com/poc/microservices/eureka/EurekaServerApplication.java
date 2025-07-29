package com.poc.microservices.eureka;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
@EnableEurekaServer  // Enables service discovery
public class EurekaServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(EurekaServerApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }

    @PostConstruct
    public void logServerIp() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            String hostAddress = ip.getHostAddress();
            logger.info("Server IP: {}", hostAddress);
            Files.writeString(Path.of("/tmp/eureka-ip.txt"), hostAddress);
        } catch (Exception e) {
            logger.error("Unable to publish in Linux ENV the host IP: {}", e.getMessage());
        }
    }
}
