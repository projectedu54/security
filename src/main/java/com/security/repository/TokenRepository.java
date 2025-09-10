package com.security.repository;

import com.security.entity.Token;
import com.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByAccessToken(String accessToken);
    Optional<Token> findByRefreshToken(String refreshToken);
    Optional<Token> findByRefreshTokenAndIpAddress(String refreshToken, String ipAddress);
    Optional<Token> findByUser(User user);
    Optional<Token> findByUserAndIpAddress(User user, String ipAddress);
    List<Token> findAllByIpAddressAndRevokedFalse(String ipAddress);
    List<Token> findAllByUserAndRevokedFalse(User user);
    List<Token> findAllByUser(User user);


}
