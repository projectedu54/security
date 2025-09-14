package com.security.service;

import com.security.dto.TokenPair;
import com.security.entity.User;
import com.security.exception.customException.InvalidJwtTokenException;
import com.security.exception.customException.TokenExpiredException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
public class JWTService {

    private SecretKey key;
    private static final long ACCESS_EXP_MS = 1000 * 60 * 30; // 30 min
    private static final long REFRESH_EXP_MS = 1000L * 60 * 60 * 24 * 30; // 30 days
    private static final String SECRET = "MySuperSecretKeyForJwtTokenThatShouldBeLongEnough123!";

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(Base64.getEncoder().encodeToString(SECRET.getBytes())));
    }

    public TokenPair generateTokenPair(User user) {
        Date now = new Date();
        Date accessExp = new Date(now.getTime() + ACCESS_EXP_MS);
        Date refreshExp = new Date(now.getTime() + REFRESH_EXP_MS);

        String accessToken = Jwts.builder()
                .setSubject(user.getUserName())
                .setIssuedAt(now)
                .setExpiration(accessExp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(user.getUserName())
                .setIssuedAt(now)
                .setExpiration(refreshExp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return new TokenPair(accessToken, refreshToken, ACCESS_EXP_MS / 1000, REFRESH_EXP_MS / 1000);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("JWT token has expired");
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtTokenException("JWT token is invalid", e);
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw e; // Rethrow so extractClaim can catch it
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtTokenException("Failed to extract claims from token", e);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}