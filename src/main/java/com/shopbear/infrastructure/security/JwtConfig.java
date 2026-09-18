package com.shopbear.infrastructure.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
// Đánh dấu class chứa cấu hình cho Spring.
public class JwtConfig {

    @Value("${shopbear.jwt.secret}")
    // Đọc JWT secret từ application.properties.
    private String jwtSecret;

    @Bean
    // Đăng ký SecretKey vào Spring Container để nơi khác có thể inject.
    public SecretKey secretKey() {

        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }
}