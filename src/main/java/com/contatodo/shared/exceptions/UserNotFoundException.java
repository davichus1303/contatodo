package com.contatodo.shared.exceptions;

/**
 * Exception thrown when a user is not found.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Creates a user not found exception.
     *
     * @param message Error message.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
