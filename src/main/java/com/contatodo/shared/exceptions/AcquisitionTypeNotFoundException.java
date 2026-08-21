package com.contatodo.shared.exceptions;

import com.contatodo.domain.exception.DomainException;

/**
 * Thrown when an acquisition type cannot be found.
 */
public class AcquisitionTypeNotFoundException extends DomainException {

    /**
     * Creates an acquisition type not found exception.
     *
     * @param message Human readable description.
     */
    public AcquisitionTypeNotFoundException(String message) {
        super(message);
    }
}
