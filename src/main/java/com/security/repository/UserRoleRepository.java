package com.security.repository;

import com.security.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

    Optional<UserRole> findByRoleName(String roleName); // correct method name

    // The next line is optional — JpaRepository already provides findById()
     Optional<UserRole> findById(Integer id);
}