package com.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenPair {
    private String accessToken;
    private String refreshToken;
    private long accessTokenExpiresIn;   // in seconds
    private long refreshTokenExpiresIn;  // in seconds
}
