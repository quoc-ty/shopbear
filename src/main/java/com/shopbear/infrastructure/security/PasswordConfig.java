package com.shopbear.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
// Đánh dấu class này là nơi chứa cấu hình cho Spring.
public class PasswordConfig {

    @Bean
    // Đăng ký PasswordEncoder vào Spring Container để class khác có thể dùng.
    public PasswordEncoder passwordEncoder() {

        // Tạo PasswordEncoder sử dụng thuật toán BCrypt.
        return new BCryptPasswordEncoder();
    }
}