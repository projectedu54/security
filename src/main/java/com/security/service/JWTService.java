package com.security.service;

import com.security.dto.TokenPair;
import com.security.entity.User;
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

    // You can also store this in application.properties
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
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Validate token by checking signature and expiration
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET.getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true; // Valid token
        } catch (JwtException | IllegalArgumentException e) {
            // invalid token
            return false;
        }
    }
}