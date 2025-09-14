// TokenExpiredException.java
package com.security.exception.customException;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
