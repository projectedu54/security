package com.security.exception.customException;

public class MaxDevicesLoggedInException extends RuntimeException {
    public MaxDevicesLoggedInException(String message) { super(message); }
}