package com.contatodo.shared.exceptions;

import com.contatodo.domain.exception.DomainException;

/**
 * Exception thrown when a user is not found.
 */
public class UserNotFoundException extends DomainException {

    /**
     * Creates a user not found exception.
     *
     * @param message Error message.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
