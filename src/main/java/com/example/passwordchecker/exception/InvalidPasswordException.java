package com.example.passwordchecker.exception;

/**
 * Custom runtime exception thrown when password input fails domain-specific validation rules.
 */
public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
