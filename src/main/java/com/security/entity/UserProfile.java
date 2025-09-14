package com.security.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "user_profile_tbl",
    indexes = {
        @Index(name = "idx_generic_user_id", columnList = "generic_user_id"),
        @Index(name = "idx_active_profile", columnList = "active_profile"),
        @Index(name = "idx_primary_profile", columnList = "primary_profile")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    // Option 1: Store JSON as plain string
    @Column(name = "profile_settings", columnDefinition = "JSON")
    private String profileSettings;

    // Option 2: Use a POJO and convert JSON <-> Object (uncomment if needed)
    // @Convert(converter = ProfileSettingsConverter.class)
    // private ProfileSettings profileSettings;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}
