package com.contatodo.shared.exceptions;

import com.contatodo.domain.exception.DomainException;

/**
 * Exception thrown when product stock is insufficient.
 */
public class InsufficientStockException extends DomainException {

    /**
     * Creates an insufficient stock exception.
     *
     * @param message Error message.
     */
    public InsufficientStockException(String message) {
        super(message);
    }
}
