package com.security.repository;

import com.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Find user by user_id (mapped as userName in entity)
    Optional<User> findByUserName(String userName);

    // Optional: Find active user by username
    Optional<User> findByUserNameAndIsActiveTrue(String userName);

    // Optional: Check if user exists by user_id
    boolean existsByUserName(String userName);
}