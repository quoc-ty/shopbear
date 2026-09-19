package com.shopbear.auth.service;

import com.shopbear.auth.dto.*;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    RefreshResponse refresh(String refreshToken); // Tạo Access Token + Refresh Token mới.

    LoginResponse login(LoginRequest request);
}
