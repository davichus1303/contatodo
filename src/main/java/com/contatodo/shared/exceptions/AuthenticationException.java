package com.contatodo.shared.exceptions;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Creates an authentication exception.
     *
     * @param message Error message.
     */
    public AuthenticationException(String message) {
        super(message);
    }
}
