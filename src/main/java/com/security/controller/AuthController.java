package com.security.controller;

import com.security.config.AuthProperties;
import com.security.dto.*;
import com.security.entity.Token;
import com.security.entity.User;
import com.security.entity.UserRole;
import com.security.exception.customException.*;
import com.security.repository.UserRepository;
import com.security.repository.TokenRepository;
import com.security.repository.UserRoleRepository;
import com.security.service.JWTService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${project.name}/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final TokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthProperties authProperties;

    public AuthController(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            TokenRepository tokenRepo,
            PasswordEncoder passwordEncoder,
            JWTService jwtService,
            AuthProperties authProperties
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.tokenRepo = tokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authProperties = authProperties;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserRequest request) {
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        UserRole role = userRoleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new InvalidRoleException("Invalid role ID"));

        User user = new User();
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // securely hash password
        user.setRole(role);
        user.setIsActive(request.getIsActive());

        userRepository.save(user);
        return ResponseEntity.ok("User has been registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletRequest servletRequest) {
        User user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isAfter(Instant.now())) {
            long minutesLeft = Duration.between(Instant.now(), user.getAccountLockedUntil()).toMinutes();
            throw new AccountLockedException("Account is locked. Try again in " + minutesLeft + " minutes.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= 5) {
                user.setAccountLockedUntil(Instant.now().plus(Duration.ofHours(24)));
            }

            userRepository.save(user);
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // Reset failed attempts on successful login
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);

        Instant nowInstant = Instant.now();
        user.setLastLogin(nowInstant);
        user.setUpdatedAt(nowInstant);
        userRepository.save(user);

        String clientIp = servletRequest.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = servletRequest.getRemoteAddr();
        }

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

        List<Token> activeTokens = tokenRepo.findAllByUserAndRevokedFalse(user).stream()
                .filter(t -> t.getAccessTokenExpiresAt().isAfter(Instant.now()))
                .toList();

        if (activeTokens.size() >= authProperties.getMaxDevices()) {
            throw new MaxDevicesLoggedInException("Maximum number of devices already logged in.");
        }

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
        String clientIp = servletRequest.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = servletRequest.getRemoteAddr();
        }

        Token storedToken = tokenRepo.findByRefreshTokenAndIpAddress(refreshToken, clientIp)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

        if (storedToken.isRevoked() || storedToken.getRefreshTokenExpiresAt().isBefore(Instant.now())) {
            tokenRepo.delete(storedToken);
            throw new RefreshTokenExpiredException("Refresh token expired");
        }

        tokenRepo.delete(storedToken);

        TokenPair newTokens = jwtService.generateTokenPair(storedToken.getUser());

        Token newTokenEntry = new Token();
        newTokenEntry.setUser(storedToken.getUser());
        newTokenEntry.setAccessToken(newTokens.getAccessToken());
        newTokenEntry.setRefreshToken(newTokens.getRefreshToken());
        newTokenEntry.setAccessTokenExpiresAt(Instant.now().plusSeconds(newTokens.getAccessTokenExpiresIn()));
        newTokenEntry.setRefreshTokenExpiresAt(Instant.now().plusSeconds(newTokens.getRefreshTokenExpiresIn()));
        newTokenEntry.setIpAddress(clientIp);
        newTokenEntry.setRevoked(false);

        tokenRepo.save(newTokenEntry);

        return ResponseEntity.ok(new AuthResponse(
                newTokens.getAccessToken(),
                newTokens.getRefreshToken(),
                newTokens.getAccessTokenExpiresIn(),
                newTokens.getRefreshTokenExpiresIn()
        ));
    }

    @PostMapping("/revoke-by-ip")
    public ResponseEntity<?> revokeTokensByIp(@RequestParam String ipAddress) {
        List<Token> tokens = tokenRepo.findAllByIpAddressAndRevokedFalse(ipAddress);

        if (tokens.isEmpty()) {
            throw new NoActiveTokensFoundException("No active tokens found for IP: " + ipAddress);
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
            throw new InvalidAuthorizationHeaderException("Invalid Authorization header");
        }

        String accessToken = authorizationHeader.substring(7);
        Token currentToken = tokenRepo.findByAccessToken(accessToken)
                .orElseThrow(() -> new TokenNotFoundException("Session not found"));

        User user = currentToken.getUser();

        if (allDevices) {
            List<Token> allUserTokens = tokenRepo.findAllByUser(user);
            tokenRepo.deleteAll(allUserTokens);
            return ResponseEntity.ok("Logged out from all devices");
        } else {
            tokenRepo.delete(currentToken);
            return ResponseEntity.ok("Logged out from current device");
        }
    }

    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidAuthorizationHeaderException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        boolean isValid = jwtService.isTokenValid(token);

        if (!isValid) {
            throw new InvalidCredentialsException("Invalid or expired token");
        }

        Instant expirationInstant = jwtService.extractExpiration(token).toInstant();
        Instant now = Instant.now();

        long expiresInMillis = expirationInstant.isAfter(now) ? expirationInstant.toEpochMilli() - now.toEpochMilli() : 0;
        long expiresInSeconds = expiresInMillis / 1000;

        return ResponseEntity.ok(new Object() {
            public final boolean valid = true;
            public final long expiresInMillisValue = expiresInMillis;
            public final long expiresInSecondsValue = expiresInSeconds;
        });
    }
}