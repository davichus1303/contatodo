package com.contatodo.shared.utils;

import com.contatodo.domain.exception.InvalidEntityStateException;

/**
 * Invariant guards used by domain entity builders to fail fast.
 *
 * <p>Every guard throws {@link InvalidEntityStateException} with the given
 * message when the corresponding requirement is not met.</p>
 */
public final class EntityValidation {

    private EntityValidation() {
    }

    /**
     * Requires a text field to be non-null and not blank after trimming.
     *
     * @param value Field value.
     * @param errorMessage Message for the thrown exception.
     */
    public static void requireNotBlank(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidEntityStateException(errorMessage);
        }
    }

    /**
     * Requires an optional number to be zero or greater when present.
     *
     * @param value Numeric value, may be null.
     * @param errorMessage Message for the thrown exception.
     */
    public static void requireNonNegative(Number value, String errorMessage) {
        if (value != null && value.doubleValue() < 0) {
            throw new InvalidEntityStateException(errorMessage);
        }
    }

    /**
     * Requires a number to be present and greater than zero.
     *
     * @param value Numeric value.
     * @param errorMessage Message for the thrown exception.
     */
    public static void requirePositive(Number value, String errorMessage) {
        if (value == null || value.doubleValue() <= 0) {
            throw new InvalidEntityStateException(errorMessage);
        }
    }
}