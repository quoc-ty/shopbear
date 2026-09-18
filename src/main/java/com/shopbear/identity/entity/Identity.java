package com.shopbear.identity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "identity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Identity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "identity")
    private List<AuthSession> authSessions;

    @OneToMany(mappedBy = "identity")
    private List<Credential> credentials;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private IdentityType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private IdentityStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}