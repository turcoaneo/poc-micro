package com.poc.microservices.main.app.config.helper;

import com.poc.microservices.JwtHelper;
import io.jsonwebtoken.JwtException;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class JwtLocalHelper {

    public String getRoleFromToken(String token, String secretKey) {
        try {
            return new JwtHelper(null).getRoleFromToken(token, secretKey);
        } catch (JwtException e) {
            throw new JwtException("Invalid or expired token", e);
        }
    }
}