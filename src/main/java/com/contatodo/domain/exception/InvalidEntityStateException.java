package com.contatodo.domain.exception;

/**
 * Thrown when an entity would be built violating one of its invariants.
 *
 * <p>Acts as the last line of defense: application-level validation should
 * catch these earlier with field details, but the domain never accepts an
 * invalid object regardless of its origin.</p>
 */
public class InvalidEntityStateException extends DomainException {

    /**
     * Creates an invalid entity state exception.
     *
     * @param message Human readable invariant violation description.
     */
    public InvalidEntityStateException(String message) {
        super(message);
    }
}
