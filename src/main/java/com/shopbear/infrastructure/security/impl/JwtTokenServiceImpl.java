package com.shopbear.infrastructure.security.impl;

import com.shopbear.identity.entity.IdentityType;
import com.shopbear.infrastructure.security.JwtTokenService;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
// Đăng ký JwtTokenServiceImpl vào Spring Container.
@RequiredArgsConstructor
// Lombok tự tạo constructor để Spring inject SecretKey.
public class JwtTokenServiceImpl implements JwtTokenService {

    private final SecretKey secretKey;

    @Override
    public String generateAccessToken(Long identityId, IdentityType type) {

        Instant now = Instant.now();

        Instant expiration =
                now.plus(15, ChronoUnit.MINUTES);

        // Tạo và ký JWT.
        return Jwts.builder()
                .subject(String.valueOf(identityId)) // sub = ID của Identity.
                .claim("type", type.name()) // type = loại Identity, ví dụ CUSTOMER.
                .issuedAt(Date.from(now)) // iat = thời điểm token được tạo.
                .expiration(Date.from(expiration)) // exp = thời điểm token hết hạn.
                .signWith(secretKey) // Ký token bằng SecretKey.
                .compact(); // Chuyển JWT thành String để trả về client.
    }


    @Override
    public String generateRefreshToken(Long identityId) {

        return Jwts.builder()
                .subject(String.valueOf(identityId)) // sub = ID của Identity.
                .id(UUID.randomUUID().toString()) // jti = ID duy nhất cho mỗi Refresh Token.
                .signWith(secretKey) // Ký token bằng SecretKey.
                .compact(); // Chuyển JWT thành String.
    }



    @Override
    public Long extractIdentityId(String token) {
        // Parse JWT và lấy claim "sub".
        String subject = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return Long.valueOf(subject); // Chuyển sub từ String thành Long.
    }

    @Override
    public boolean isValid(String token) {
        try {
            // Parse thành công nghĩa là chữ ký hợp lệ và token chưa hết hạn.
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception exception) {
            // Token không hợp lệ hoặc đã hết hạn.
            return false;
        }
    }
}

