package com.security.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_tbl")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "generic_id")
    private Integer id;

    @Column(name = "user_id", nullable = false, unique = true, length = 100)
    private String userName;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String password;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_login")
    private Instant lastLogin;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private Instant accountLockedUntil;

    @Column(
            name = "created_at",
            insertable = false,
            updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            insertable = false,
            updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
    )
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_role_id_fk", nullable = false)
    private UserRole role;

    // No-args constructor
    public User() {
    }

    // All-args constructor
    public User(Integer id, String userName, String password, Boolean isActive, Instant lastLogin,
                Integer failedLoginAttempts, Instant accountLockedUntil, Instant createdAt,
                Instant updatedAt, UserRole role) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.isActive = isActive;
        this.lastLogin = lastLogin;
        this.failedLoginAttempts = failedLoginAttempts;
        this.accountLockedUntil = accountLockedUntil;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.role = role;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Instant lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Instant getAccountLockedUntil() {
        return accountLockedUntil;
    }

    public void setAccountLockedUntil(Instant accountLockedUntil) {
        this.accountLockedUntil = accountLockedUntil;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    // Usually createdAt is not settable from code, but I’m adding for completeness
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // Usually updatedAt is managed by DB triggers, but setter is here if needed
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}