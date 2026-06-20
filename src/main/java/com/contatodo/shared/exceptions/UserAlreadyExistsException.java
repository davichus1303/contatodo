package com.contatodo.shared.exceptions;

/**
 * Exception thrown when a user already exists.
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Creates a user already exists exception.
     *
     * @param message Error message.
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
