package com.shopbear.auth.controller;

import com.shopbear.auth.dto.*;
import com.shopbear.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return authService.register(request);
    }
    @PostMapping("/refresh")
    public RefreshResponse refresh(
            @RequestBody RefreshRequest request) {

        return authService.refresh(
                request.getRefreshToken()
        ); // Gửi Refresh Token xuống Service để kiểm tra và rotation.
    }
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request){

        return authService.login(request);
    }
}

