package com.shopbear.infrastructure.security;

import com.shopbear.identity.entity.IdentityType;

public interface JwtTokenService {
    String generateAccessToken(Long identityId, IdentityType type);

    String generateRefreshToken(Long identityId );

    Long extractIdentityId(String token);

    boolean isValid(String token);
}
