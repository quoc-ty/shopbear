package com.shopbear.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable()) // API dùng JWT, chưa dùng CSRF token.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/register",
                                "/auth/refresh"
                        )
                        .permitAll() // Cho phép đăng ký không cần đăng nhập.
                        .anyRequest().authenticated() // Các API khác tạm thời yêu cầu authentication.
                );

        return http.build(); // Tạo SecurityFilterChain cho Spring Security.
    }
}
