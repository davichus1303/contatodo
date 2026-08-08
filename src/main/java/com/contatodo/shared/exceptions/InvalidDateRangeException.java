package com.contatodo.shared.exceptions;

/**
 * Exception thrown when an invalid date range is provided.
 */
public class InvalidDateRangeException extends RuntimeException {

    /**
     * Creates an invalid date range exception.
     *
     * @param message Error message.
     */
    public InvalidDateRangeException(String message) {
        super(message);
    }
}
