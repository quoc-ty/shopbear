package com.shopbear.identity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "credential",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_credential_provider_identifier",
                        columnNames = {"provider", "identifier"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "identity_id", nullable = false)
    private Identity identity;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private CredentialProvider provider;

    @Column(name = "identifier", nullable = false, length = 254)
    private String identifier;

    @Column(name = "secret_hash", length = 100)
    private String secretHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}