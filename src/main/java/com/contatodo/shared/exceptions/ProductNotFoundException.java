package com.contatodo.shared.exceptions;

/**
 * Exception thrown when a product is not found.
 */
public class ProductNotFoundException extends RuntimeException {

    /**
     * Creates a product not found exception.
     *
     * @param message Error message.
     */
    public ProductNotFoundException(String message) {
        super(message);
    }
}
