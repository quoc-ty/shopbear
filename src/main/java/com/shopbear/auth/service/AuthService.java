package com.shopbear.auth.service;

import com.shopbear.auth.dto.RefreshResponse;
import com.shopbear.auth.dto.RegisterRequest;
import com.shopbear.auth.dto.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    RefreshResponse refresh(String refreshToken); // Tạo Access Token + Refresh Token mới.
}
