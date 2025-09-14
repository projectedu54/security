package com.security.exception.customException;

public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException(String message) { super(message); }
}