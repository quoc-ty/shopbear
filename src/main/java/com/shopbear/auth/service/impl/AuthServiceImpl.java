package com.shopbear.auth.service.impl;

import com.shopbear.auth.config.AuthProperties;
import com.shopbear.auth.dto.*;
import com.shopbear.auth.exception.EmailAlreadyExistsException;
import com.shopbear.auth.exception.InvalidCredentialsException;
import com.shopbear.auth.service.AuthService;
import com.shopbear.auth.service.EmailNormalizer;
import com.shopbear.customer.entity.Customer;
import com.shopbear.customer.repository.CustomerRepository;
import com.shopbear.identity.entity.*;
import com.shopbear.identity.repository.AuthSessionRepository;
import com.shopbear.identity.repository.CredentialRepository;
import com.shopbear.identity.repository.IdentityRepository;
import com.shopbear.infrastructure.security.JwtTokenService;
import com.shopbear.infrastructure.security.TokenHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final IdentityRepository identityRepository;
    private final CredentialRepository credentialRepository;
    private final CustomerRepository customerRepository;
    private final EmailNormalizer emailNormalizer;
    private final AuthSessionRepository authSessionRepository;
    private final JwtTokenService jwtTokenService;
    private final TokenHasher tokenHasher; // Dùng để hash Refresh Token bằng SHA-256.
    private final AuthProperties authProperties; // Đọc các cấu hình liên quan đến authentication.

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        // 1. Chuẩn hóa email trước khi lưu và kiểm tra.
        String normalizedEmail =
                emailNormalizer.normalize(request.getEmail());

        // 2. Kiểm tra email đã tồn tại chưa.
        Optional<Credential> existingCredential =
                credentialRepository.findByProviderAndIdentifier(
                        CredentialProvider.PASSWORD,
                        normalizedEmail
                );

        if (existingCredential.isPresent()) {
            throw new EmailAlreadyExistsException("Unable to complete registration");
        }

        // 3. Tạo Identity cho Customer.
        Identity identity = new Identity();
        identity.setType(IdentityType.CUSTOMER);
        identity.setStatus(IdentityStatus.ACTIVE);
        identity.setCreatedAt(LocalDateTime.now());
        identity.setUpdatedAt(LocalDateTime.now());

        identity = identityRepository.save(identity);

        // 4. Hash password trước khi lưu DB.
        String passwordHash =
                passwordEncoder.encode(request.getPassword());

        // 5. Tạo Credential dùng PASSWORD provider.
        Credential credential = new Credential();
        credential.setIdentity(identity);
        credential.setProvider(CredentialProvider.PASSWORD);
        credential.setIdentifier(normalizedEmail);
        credential.setSecretHash(passwordHash);
        credential.setCreatedAt(LocalDateTime.now());
        credential.setUpdatedAt(LocalDateTime.now());

        credentialRepository.save(credential);

        // 6. Tạo Customer profile.
        Customer customer = new Customer();
        customer.setIdentity(identity);
        customer.setFullName(request.getFullName());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        customer = customerRepository.save(customer);

        // 7. Tạo Access Token ngay sau khi đăng ký thành công.
        String accessToken = jwtTokenService.generateAccessToken(
                        identity.getId(),
                        identity.getType()
                );

        // 8. Tạo Refresh Token.
        String refreshToken = jwtTokenService.generateRefreshToken(identity.getId());

        // 9. Hash Refresh Token trước khi lưu DB.
        String refreshTokenHash = tokenHasher.hash(refreshToken); // Hash Refresh Token bằng SHA-256.

        // 10. Tạo AuthSession để quản lý phiên đăng nhập.
        AuthSession authSession = new AuthSession();
        authSession.setIdentity(identity);
        authSession.setRefreshTokenHash(refreshTokenHash);
        authSession.setLastActivityAt(LocalDateTime.now());
        authSession.setCreatedAt(LocalDateTime.now());

        authSessionRepository.save(authSession);

        // 11. Trả Customer + Access Token + Refresh Token cho Client.
        return new RegisterResponse(
                new com.shopbear.customer.dto.CustomerResponse(
                        customer.getId(),
                        customer.getFullName()
                ),
                accessToken,
                refreshToken
        );
    }

    @Override
    public RefreshResponse refresh(String refreshToken) {

        // 1. Kiểm tra Refresh Token có hợp lệ không.
        if (!jwtTokenService.isValid(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // 2. Lấy Identity ID từ Refresh Token.
        Long identityId = jwtTokenService.extractIdentityId(refreshToken);

        // 3. Tìm AuthSession của Identity.
        Optional<AuthSession> optionalSession =
                authSessionRepository.findByIdentityId(identityId);

        if (optionalSession.isEmpty()) {
            throw new RuntimeException("Session not found");
        }

        AuthSession authSession = optionalSession.get();

        // 4. Kiểm tra session đã bị revoke chưa.
        if (authSession.getRevokedAt() != null) {
            throw new RuntimeException("Session revoked");
        }

        // 5. Kiểm tra Refresh Token có đúng token đang lưu trong session không.
        if (!tokenHasher.matches(
                refreshToken,
                authSession.getRefreshTokenHash())) {

            throw new RuntimeException("Invalid refresh token");
        }

        // 6. Kiểm tra timeout inactivity.
        LocalDateTime now = LocalDateTime.now();

        if (authSession.getLastActivityAt()
                .plus(authProperties.getInactivityTimeout())
                .isBefore(now)) {

            throw new RuntimeException("Session expired"); // Session hết hạn khi vượt quá inactivity timeout.
        }

        // 7. Lấy Identity để biết IdentityType.
        Identity identity = authSession.getIdentity();

        // 8. Tạo Access Token mới.
        String newAccessToken =
                jwtTokenService.generateAccessToken(
                        identity.getId(),
                        identity.getType()
                );

        // 9. Tạo Refresh Token mới (rotation).
        String newRefreshToken =
                jwtTokenService.generateRefreshToken(identity.getId());

        // 10. Hash Refresh Token mới trước khi lưu DB.
        String newRefreshTokenHash =
                tokenHasher.hash(newRefreshToken); // Hash Refresh Token mới bằng SHA-256.
        // 11. Cập nhật session.
        authSession.setRefreshTokenHash(newRefreshTokenHash);
        authSession.setLastActivityAt(now);

        authSessionRepository.save(authSession);

        // 12. Trả về 2 token mới cho Client.
        return new RefreshResponse(
                newAccessToken,
                newRefreshToken
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        String normalizedEmail = emailNormalizer.normalize(request.getEmail());
        Credential credential =
                credentialRepository.findByProviderAndIdentifier(
                        CredentialProvider.PASSWORD,
                        normalizedEmail).orElseThrow(
                                ()->new InvalidCredentialsException("Email or password is incorrect")
                );

        if(!passwordEncoder.matches(request.getPassword(), credential.getSecretHash())){

            throw new InvalidCredentialsException("Email or password is incorrect"
            );
        }

        Identity identity = credential.getIdentity();
        if (identity.getStatus() != IdentityStatus.ACTIVE) {
            throw new InvalidCredentialsException(
                    "Email or password is incorrect"
            );
        }

        String accessToken = jwtTokenService.generateAccessToken(identity.getId(), identity.getType());

        String refeshToken = jwtTokenService.generateRefreshToken(identity.getId());
        String refreshTokenHash = tokenHasher.hash(refeshToken);

        LocalDateTime now = LocalDateTime.now();

        AuthSession authSession = new AuthSession();
        authSession.setIdentity(identity);
        authSession.setRefreshTokenHash(refreshTokenHash);
        authSession.setLastActivityAt(now);
        authSession.setCreatedAt(now);

        authSessionRepository.save(authSession);

        return new LoginResponse(accessToken,refeshToken);
    }


}