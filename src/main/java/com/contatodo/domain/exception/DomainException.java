package com.contatodo.domain.exception;

/**
 * Base class for all domain rule violations.
 *
 * <p>Thrown when an invariant of the domain is broken regardless of the
 * origin of the data (REST request, importer, scheduled job). Subclasses
 * keep the exact message contract exposed by the API.</p>
 */
public abstract class DomainException extends RuntimeException {

    /**
     * Creates a domain exception.
     *
     * @param message Human readable violation description.
     */
    protected DomainException(String message) {
        super(message);
    }
}
