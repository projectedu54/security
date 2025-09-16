package com.security.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "user_profile_tbl",
        indexes = {
                @Index(name = "idx_generic_user_id", columnList = "generic_user_id"),
                @Index(name = "idx_active_profile", columnList = "active_profile"),
                @Index(name = "idx_primary_profile", columnList = "primary_profile")
        }
)
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id", nullable = false, updatable = false)
    private Integer profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generic_user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_profile_user"))
    private User user;

    @Column(name = "active_profile", nullable = false)
    private Boolean activeProfile = true;

    @Column(name = "primary_profile", nullable = false)
    private Boolean primaryProfile = false;

    @Column(name = "switch_profile", nullable = false)
    private Boolean switchProfile = false;

    @Column(name = "profile_settings", columnDefinition = "JSON")
    private String profileSettings;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // No-args constructor
    public UserProfile() {}

    // All-args constructor
    public UserProfile(Integer profileId, User user, Boolean activeProfile, Boolean primaryProfile,
                       Boolean switchProfile, String profileSettings,
                       Instant createdAt, Instant updatedAt) {
        this.profileId = profileId;
        this.user = user;
        this.activeProfile = activeProfile;
        this.primaryProfile = primaryProfile;
        this.switchProfile = switchProfile;
        this.profileSettings = profileSettings;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(Boolean activeProfile) {
        this.activeProfile = activeProfile;
    }

    public Boolean getPrimaryProfile() {
        return primaryProfile;
    }

    public void setPrimaryProfile(Boolean primaryProfile) {
        this.primaryProfile = primaryProfile;
    }

    public Boolean getSwitchProfile() {
        return switchProfile;
    }

    public void setSwitchProfile(Boolean switchProfile) {
        this.switchProfile = switchProfile;
    }

    public String getProfileSettings() {
        return profileSettings;
    }

    public void setProfileSettings(String profileSettings) {
        this.profileSettings = profileSettings;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // Builder pattern
    public static class Builder {
        private Integer profileId;
        private User user;
        private Boolean activeProfile = true;
        private Boolean primaryProfile = false;
        private Boolean switchProfile = false;
        private String profileSettings;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder() {}

        public Builder profileId(Integer profileId) {
            this.profileId = profileId;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder activeProfile(Boolean activeProfile) {
            this.activeProfile = activeProfile;
            return this;
        }

        public Builder primaryProfile(Boolean primaryProfile) {
            this.primaryProfile = primaryProfile;
            return this;
        }

        public Builder switchProfile(Boolean switchProfile) {
            this.switchProfile = switchProfile;
            return this;
        }

        public Builder profileSettings(String profileSettings) {
            this.profileSettings = profileSettings;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public UserProfile build() {
            return new UserProfile(profileId, user, activeProfile, primaryProfile, switchProfile, profileSettings, createdAt, updatedAt);
        }
    }
}