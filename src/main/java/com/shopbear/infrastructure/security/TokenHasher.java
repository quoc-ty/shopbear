package com.shopbear.infrastructure.security;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
@Service // Đăng ký TokenHasher thành Spring Bean để có thể inject.
public class TokenHasher {



    public boolean matches(String token, String tokenHash) {
        return hash(token).equals(tokenHash); // Hash token rồi so sánh với hash đang lưu trong DB.
    }


    public String hash(String token) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256"); // Hash token bằng SHA-256.

            byte[] hash =
                    digest.digest(token.getBytes(StandardCharsets.UTF_8)); // Chuyển token thành bytes rồi hash.

            StringBuilder hex = new StringBuilder();

            for (byte b : hash) {
                hex.append(String.format("%02x", b)); // Chuyển mỗi byte thành hex.
            }

            return hex.toString();

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm not available", exception);
        }
    }
}
