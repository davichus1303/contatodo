package com.contatodo.shared.exceptions;

import com.contatodo.domain.exception.DomainException;

/**
 * Exception thrown when the authenticated role has no access to a module or no
 * permission for the requested action over it.
 *
 * <p>Mapped to HTTP 403 so the client can tell an authorization failure apart
 * from a missing resource (404) or a bad payload (400).</p>
 */
public class AccessDeniedException extends DomainException {

    /**
     * Creates an access denied exception.
     *
     * @param message Human readable description.
     */
    public AccessDeniedException(String message) {
        super(message);
    }
}
