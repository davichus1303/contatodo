package com.contatodo.shared.exceptions;

import com.contatodo.domain.exception.DomainException;

/**
 * Exception thrown when a sale does not generate profit.
 */
public class SaleWithoutProfitException extends DomainException {

    /**
     * Creates a sale without profit exception.
     *
     * @param message Error message.
     */
    public SaleWithoutProfitException(String message) {
        super(message);
    }
}
