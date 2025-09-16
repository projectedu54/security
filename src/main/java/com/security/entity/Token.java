package com.security.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
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

    // No-args constructor
    public Token() {
    }

    // All-args constructor
    public Token(Long id, User user, String accessToken, String refreshToken, Instant accessTokenExpiresAt, Instant refreshTokenExpiresAt, String ipAddress, boolean revoked, Instant creationDate, Instant updDate) {
        this.id = id;
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
        this.ipAddress = ipAddress;
        this.revoked = revoked;
        this.creationDate = creationDate;
        this.updDate = updDate;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Instant getAccessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    public void setAccessTokenExpiresAt(Instant accessTokenExpiresAt) {
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    public Instant getRefreshTokenExpiresAt() {
        return refreshTokenExpiresAt;
    }

    public void setRefreshTokenExpiresAt(Instant refreshTokenExpiresAt) {
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public Instant getUpdDate() {
        return updDate;
    }

    // Builder remains unchanged
    public static class Builder {
        private Long id;
        private User user;
        private String accessToken;
        private String refreshToken;
        private Instant accessTokenExpiresAt;
        private Instant refreshTokenExpiresAt;
        private String ipAddress;
        private boolean revoked;
        private Instant creationDate;
        private Instant updDate;

        public Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder accessTokenExpiresAt(Instant accessTokenExpiresAt) {
            this.accessTokenExpiresAt = accessTokenExpiresAt;
            return this;
        }

        public Builder refreshTokenExpiresAt(Instant refreshTokenExpiresAt) {
            this.refreshTokenExpiresAt = refreshTokenExpiresAt;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder revoked(boolean revoked) {
            this.revoked = revoked;
            return this;
        }

        public Builder creationDate(Instant creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder updDate(Instant updDate) {
            this.updDate = updDate;
            return this;
        }

        public Token build() {
            return new Token(id, user, accessToken, refreshToken, accessTokenExpiresAt, refreshTokenExpiresAt, ipAddress, revoked, creationDate, updDate);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}