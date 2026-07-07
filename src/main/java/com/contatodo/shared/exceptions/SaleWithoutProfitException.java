package com.contatodo.shared.exceptions;

/**
 * Exception thrown when a sale does not generate profit.
 */
public class SaleWithoutProfitException extends RuntimeException {

    /**
     * Creates a sale without profit exception.
     *
     * @param message Error message.
     */
    public SaleWithoutProfitException(String message) {
        super(message);
    }
}
