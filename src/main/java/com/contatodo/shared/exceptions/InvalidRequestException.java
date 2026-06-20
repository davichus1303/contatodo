package com.contatodo.shared.exceptions;

import java.util.List;

/**
 * Exception thrown when request validation fails.
 */
public class InvalidRequestException extends RuntimeException {

    private final List<String> details;

    /**
     * Creates an invalid request exception.
     *
     * @param message Error message.
     * @param details Validation error details.
     */
    public InvalidRequestException(String message, List<String> details) {
        super(message);
        this.details = details;
    }

    /**
     * Returns validation error details.
     *
     * @return Validation error details.
     */
    public List<String> getDetails() {
        return details;
    }
}
