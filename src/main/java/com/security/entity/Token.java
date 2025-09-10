package com.security.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "token_tbl")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_generic_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private User user;

    @Column(unique = true)
    private String accessToken;

    @Column(unique = true)
    private String refreshToken;

    private Instant accessTokenExpiresAt;
    private Instant refreshTokenExpiresAt;
    private String ipAddress;
    private boolean revoked;

    @CreationTimestamp
    private Instant creationDate;

    @UpdateTimestamp
    private Instant updDate; // Auto-updated when row changes
}
