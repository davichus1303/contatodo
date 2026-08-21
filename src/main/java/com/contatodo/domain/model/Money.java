package com.contatodo.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object representing a monetary amount with two decimal places.
 *
 * <p>All arithmetic is performed with {@link BigDecimal} to avoid floating
 * point drift when comparing costs against sale prices.</p>
 */
public final class Money {

    private static final int SCALE = 2;

    private final BigDecimal amount;

    /**
     * Creates a money instance.
     *
     * @param amount Amount with fixed scale.
     */
    private Money(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Creates money from a boxed double.
     *
     * @param value Raw amount; must not be null.
     * @return Money instance.
     * @throws NullPointerException if the value is null.
     */
    public static Money of(Double value) {
        Objects.requireNonNull(value, "Money value must not be null");
        return new Money(BigDecimal.valueOf(value).setScale(SCALE, RoundingMode.HALF_UP));
    }

    /**
     * Creates zero money.
     *
     * @return Zero amount.
     */
    public static Money zero() {
        return new Money(BigDecimal.ZERO.setScale(SCALE, RoundingMode.UNNECESSARY));
    }

    /**
     * Adds another amount.
     *
     * @param other Amount to add.
     * @return New summed instance.
     */
    public Money add(Money other) {
        return new Money(amount.add(other.amount).setScale(SCALE, RoundingMode.HALF_UP));
    }

    /**
     * Multiplies by an integer quantity.
     *
     * @param quantity Quantity multiplier.
     * @return New multiplied instance.
     */
    public Money multiply(int quantity) {
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)).setScale(SCALE, RoundingMode.HALF_UP));
    }

    /**
     * Divides by an integer divisor rounding half up.
     *
     * @param divisor Divisor; must be positive.
     * @return New divided instance.
     */
    public Money divide(int divisor) {
        if (divisor <= 0) {
            throw new IllegalArgumentException("Divisor must be positive");
        }
        return new Money(amount.divide(BigDecimal.valueOf(divisor), SCALE, RoundingMode.HALF_UP));
    }

    /**
     * Checks whether this amount is strictly greater than another.
     *
     * @param other Amount to compare against.
     * @return True when greater.
     */
    public boolean isGreaterThan(Money other) {
        return amount.compareTo(other.amount) > 0;
    }

    /**
     * Gets the raw double representation used by persistence and DTOs.
     *
     * @return Amount as double.
     */
    public Double toDouble() {
        return amount.doubleValue();
    }
}
