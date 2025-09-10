package com.security.controller;

import com.security.config.AuthProperties;
import com.security.dto.*;
import com.security.entity.Token;
import com.security.entity.User;
import com.security.entity.UserRole;
import com.security.repository.UserRepository;
import com.security.repository.TokenRepository;
import com.security.repository.UserRoleRepository;
import com.security.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${project.name}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final TokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthProperties authProperties;


    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserRequest request) {
        try {
            // Check if user already exists
            if (userRepository.findByUserName(request.getUserName()).isPresent()) {
                return ResponseEntity.badRequest().body("Username already exists");
            }

            // Fetch role
            UserRole role = userRoleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role ID"));

            // Build User entity
            User user = new User();
            user.setUserName(request.getUserName());
            user.setPassword(passwordEncoder.encode(request.getPassword())); // securely hash password
            user.setRole(role);
            user.setIsActive(request.getIsActive());

            userRepository.save(user);
            return ResponseEntity.ok("User has been registered successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to register user: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletRequest servletRequest) {
        Optional<User> userOpt = userRepository.findByUserName(request.getUserName());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        User user = userOpt.get();

        // Check if account is locked
        if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isAfter(Instant.now())) {
            long minutesLeft = Duration.between(Instant.now(), user.getAccountLockedUntil()).toMinutes();
            return ResponseEntity.status(403).body("Account is locked. Try again in " + minutesLeft + " minutes.");
        }

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= 5) { // You can set this to 10 as per your DB constraint
                user.setAccountLockedUntil(Instant.now().plus(Duration.ofHours(24)));
            }

            userRepository.save(user);
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // Reset failed attempts on successful login
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        // Set lastLogin in IST
        user.setLastLogin(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        user.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        userRepository.save(user);

        // Extract client IP
        String clientIp = servletRequest.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = servletRequest.getRemoteAddr();
        }

        // Check for existing valid token
        Optional<Token> existingTokenOpt = tokenRepo.findByUserAndIpAddress(user, clientIp);
        if (existingTokenOpt.isPresent()) {
            Token existingToken = existingTokenOpt.get();
            if (!existingToken.isRevoked() && existingToken.getAccessTokenExpiresAt().isAfter(Instant.now())) {
                long accessTokenExpiresIn = existingToken.getAccessTokenExpiresAt().getEpochSecond() - Instant.now().getEpochSecond();
                long refreshTokenExpiresIn = existingToken.getRefreshTokenExpiresAt().getEpochSecond() - Instant.now().getEpochSecond();

                return ResponseEntity.ok(new AuthResponse(
                        existingToken.getAccessToken(),
                        existingToken.getRefreshToken(),
                        accessTokenExpiresIn,
                        refreshTokenExpiresIn
                ));
            } else {
                tokenRepo.delete(existingToken);
            }
        }

        // Check active sessions
        List<Token> activeTokens = tokenRepo.findAllByUserAndRevokedFalse(user).stream()
                .filter(t -> t.getAccessTokenExpiresAt().isAfter(Instant.now()))
                .toList();

        if (activeTokens.size() >= authProperties.getMaxDevices()) {
            return ResponseEntity.status(403).body("Maximum number of devices already logged in.");
        }

        // Generate and save new token
        TokenPair tokens = jwtService.generateTokenPair(user);

        Token tokenEntity = Token.builder()
                .user(user)
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .accessTokenExpiresAt(Instant.now().plusSeconds(tokens.getAccessTokenExpiresIn()))
                .refreshTokenExpiresAt(Instant.now().plusSeconds(tokens.getRefreshTokenExpiresIn()))
                .ipAddress(clientIp)
                .revoked(false)
                .build();

        tokenRepo.save(tokenEntity);

        return ResponseEntity.ok(new AuthResponse(
                tokens.getAccessToken(),
                tokens.getRefreshToken(),
                tokens.getAccessTokenExpiresIn(),
                tokens.getRefreshTokenExpiresIn()
        ));
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Refresh-Token") String refreshToken, HttpServletRequest servletRequest) {
        // taking the ip address of machine
        String clientIp = servletRequest.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = servletRequest.getRemoteAddr();
        }

        Optional<Token> tokenOpt = tokenRepo.findByRefreshTokenAndIpAddress(refreshToken,clientIp);

        if (tokenOpt.isEmpty()) {
            String message ="Invalid refresh token";
        //    logger.trace(className,methodname,message); implement this
            return ResponseEntity.status(401).body(message);
        }

        Token storedToken = tokenOpt.get();

        // Check if refresh token is expired
        if ( storedToken.isRevoked() || storedToken.getRefreshTokenExpiresAt().isBefore(Instant.now())) {
            tokenRepo.delete(storedToken); // Clean up expired tokens
            return ResponseEntity.status(401).body("Refresh token expired");
        }

        // Invalidate old token pair (delete)
        tokenRepo.delete(storedToken);

        // Generate new token pair
        TokenPair newTokens = jwtService.generateTokenPair(storedToken.getUser());

        // Save new tokens in DB
        Token newTokenEntry = new Token();
        newTokenEntry.setUser(storedToken.getUser());
        newTokenEntry.setAccessToken(newTokens.getAccessToken());
        newTokenEntry.setRefreshToken(newTokens.getRefreshToken());
        newTokenEntry.setAccessTokenExpiresAt(Instant.now().plusSeconds(newTokens.getAccessTokenExpiresIn()));
        newTokenEntry.setRefreshTokenExpiresAt(Instant.now().plusSeconds(newTokens.getRefreshTokenExpiresIn()));
        newTokenEntry.setIpAddress(clientIp);
        newTokenEntry.setRevoked(false);

        tokenRepo.save(newTokenEntry);

        return ResponseEntity.ok(new AuthResponse(newTokens.getAccessToken(),
                newTokens.getRefreshToken(),
                newTokens.getAccessTokenExpiresIn(),
                newTokens.getRefreshTokenExpiresIn()));

    }

    @PostMapping("/revoke-by-ip")
    public ResponseEntity<?> revokeTokensByIp(@RequestParam String ipAddress) {
        List<Token> tokens = tokenRepo.findAllByIpAddressAndRevokedFalse(ipAddress);

        if (tokens.isEmpty()) {
            return ResponseEntity.status(404).body("No active tokens found for IP: " + ipAddress);
        }

        tokens.forEach(token -> token.setRevoked(true));
        tokenRepo.saveAll(tokens);

        return ResponseEntity.ok("Revoked " + tokens.size() + " token(s) for IP: " + ipAddress);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "false") boolean allDevices) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid Authorization header");
        }

        String accessToken = authorizationHeader.substring(7); // Remove "Bearer "
        Optional<Token> tokenOpt = tokenRepo.findByAccessToken(accessToken);

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Session not found");
        }

        Token currentToken = tokenOpt.get();
        User user = currentToken.getUser();

        if (allDevices) {
            // Logout from all devices — delete all tokens for this user
            List<Token> allUserTokens = tokenRepo.findAllByUser(user);
            tokenRepo.deleteAll(allUserTokens);
            return ResponseEntity.ok("Logged out from all devices");
        } else {
            // Logout from current session only
            tokenRepo.delete(currentToken);
            return ResponseEntity.ok("Logged out from current device");
        }
    }

    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        boolean isValid = jwtService.isTokenValid(token);

        if (!isValid) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        // Extract expiration time
        Instant expirationInstant = jwtService.extractExpiration(token).toInstant();
        Instant now = Instant.now();

        long expiresInMillis = expirationInstant.isAfter(now) ? expirationInstant.toEpochMilli() - now.toEpochMilli() : 0;
        long expiresInSeconds = expiresInMillis / 1000;

        // Response object
        return ResponseEntity.ok(
                new Object() {
                    public final boolean valid = true;
                    public final long expiresInMillisValue = expiresInMillis;
                    public final long expiresInSecondsValue = expiresInSeconds;
                }
        );

    }





}
