package com.shopbear.identity.repository;

import com.shopbear.identity.entity.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {

    Optional<AuthSession> findByIdentityId(Long identityId); // Tìm session theo Identity ID.
}