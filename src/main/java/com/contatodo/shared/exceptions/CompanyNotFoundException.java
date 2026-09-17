package com.contatodo.shared.exceptions;

import com.contatodo.domain.exception.DomainException;

/**
 * Exception thrown when a company is not found.
 */
public class CompanyNotFoundException extends DomainException {

    /**
     * Creates a company not found exception.
     *
     * @param message Error message.
     */
    public CompanyNotFoundException(String message) {
        super(message);
    }
}
