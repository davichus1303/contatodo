package com.contatodo.shared.exceptions;

/**
 * Exception thrown when product stock is insufficient.
 */
public class InsufficientStockException extends RuntimeException {

    /**
     * Creates an insufficient stock exception.
     *
     * @param message Error message.
     */
    public InsufficientStockException(String message) {
        super(message);
    }
}
