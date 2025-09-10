package com.security.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "user_types_tbl")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_type_id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_choice", nullable = false)
    private UserChoice userChoice;

    @Column(name = "type_name", nullable = false, unique = true)
    private String typeName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false)
    private Timestamp updatedAt;

    // One type can have many roles
    @OneToMany(mappedBy = "userType", fetch = FetchType.LAZY)
    private List<UserRole> roles;

    public enum UserChoice {
        individual,
        group
    }
}
