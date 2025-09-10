package com.security.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "user_roles_tbl")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    private Integer id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false)
    private Timestamp updatedAt;

    // FK to user_types_tbl
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_types_tbl_id_fk", nullable = false)
    private UserType userType;

    // One role can be assigned to many users
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<User> users;
}
