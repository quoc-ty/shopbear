package com.shopbear.identity.repository;

import com.shopbear.identity.entity.Credential;
import com.shopbear.identity.entity.CredentialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {
    Optional<Credential> findByProviderAndIdentifier(
            CredentialProvider provider,
            String identifier
    );
}