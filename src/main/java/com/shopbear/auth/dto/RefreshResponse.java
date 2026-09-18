package com.shopbear.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshResponse {

    private String accessToken;  // Access Token mới.
    private String refreshToken; // Refresh Token mới sau khi rotation.
}

