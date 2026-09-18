package com.shopbear.auth.controller;

import com.shopbear.auth.dto.RefreshRequest;
import com.shopbear.auth.dto.RefreshResponse;
import com.shopbear.auth.dto.RegisterRequest;
import com.shopbear.auth.dto.RegisterResponse;
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

}

