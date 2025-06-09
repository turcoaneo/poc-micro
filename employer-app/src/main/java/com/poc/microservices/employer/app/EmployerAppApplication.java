package com.poc.microservices.employer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EmployerAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployerAppApplication.class, args);
    }

}
