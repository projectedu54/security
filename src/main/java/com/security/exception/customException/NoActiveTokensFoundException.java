package com.security.exception.customException;

public class NoActiveTokensFoundException extends RuntimeException {
    public NoActiveTokensFoundException(String message) { super(message); }
}