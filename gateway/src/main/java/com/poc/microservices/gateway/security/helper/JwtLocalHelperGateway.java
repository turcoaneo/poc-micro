package com.poc.microservices.gateway.security.helper;

import com.poc.microservices.JwtHelper;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class JwtLocalHelperGateway {

    public String getRoleFromToken(String token, String secretKey) {
        return new JwtHelper(null).getRoleFromToken(token, secretKey);
    }
}