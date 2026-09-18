package com.contatodo.shared.exceptions;

import com.contatodo.domain.exception.DomainException;

/**
 * Generic exception thrown when a domain resource cannot be found.
 *
 * <p>This single type replaces the per-resource not-found exceptions, so the
 * error handling layer never has to grow per module. The message (and
 * therefore the API contract) is preserved.</p>
 */
public class ResourceNotFoundException extends DomainException {

    /**
     * Creates a resource not found exception.
     *
     * @param message Human readable description.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a resource not found exception with a default message.
     *
     * @param resource Resource kind, e.g. "user".
     * @param id Resource identifier that was not found.
     */
    public ResourceNotFoundException(String resource, String id) {
        super(String.format("%s not found with id: %s", resource, id));
    }
}