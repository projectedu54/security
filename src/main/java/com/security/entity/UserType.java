package com.security.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "user_types_tbl")
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

    @OneToMany(mappedBy = "userType", fetch = FetchType.LAZY)
    private List<UserRole> roles;

    // No-args constructor
    public UserType() {
    }

    // All-args constructor
    public UserType(Integer id, UserChoice userChoice, String typeName, String description, Boolean isActive,
                    Timestamp createdAt, Timestamp updatedAt, List<UserRole> roles) {
        this.id = id;
        this.userChoice = userChoice;
        this.typeName = typeName;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.roles = roles;
    }

    // Getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserChoice getUserChoice() {
        return userChoice;
    }

    public void setUserChoice(UserChoice userChoice) {
        this.userChoice = userChoice;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserType{" +
                "id=" + id +
                ", userChoice=" + userChoice +
                ", typeName='" + typeName + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", roles=" + roles +
                '}';
    }

    public enum UserChoice {
        individual,
        group
    }
}
