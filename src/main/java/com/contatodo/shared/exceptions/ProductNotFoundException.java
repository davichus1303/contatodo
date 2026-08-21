package com.contatodo.shared.exceptions;

import com.contatodo.domain.exception.DomainException;

/**
 * Exception thrown when a product is not found.
 */
public class ProductNotFoundException extends DomainException {

    /**
     * Creates a product not found exception.
     *
     * @param message Error message.
     */
    public ProductNotFoundException(String message) {
        super(message);
    }
}
