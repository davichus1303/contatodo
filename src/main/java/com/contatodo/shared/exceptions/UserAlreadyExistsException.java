package com.contatodo.shared.exceptions;

import com.contatodo.domain.exception.DomainException;

/**
 * Exception thrown when a user already exists.
 */
public class UserAlreadyExistsException extends DomainException {

    /**
     * Creates a user already exists exception.
     *
     * @param message Error message.
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
