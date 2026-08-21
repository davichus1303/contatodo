package com.contatodo.domain.model;

import java.util.regex.Pattern;

/**
 * Value object representing a valid email address.
 *
 * <p>Instances are immutable and can only exist in a valid state.</p>
 */
public final class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    /**
     * Creates an email value object.
     *
     * @param value Validated email text.
     */
    private Email(String value) {
        this.value = value;
    }

    /**
     * Creates an email from raw input validating its format.
     *
     * @param value Raw email text.
     * @return Validated email.
     * @throws IllegalArgumentException if the value is null, blank or malformed.
     */
    public static Email of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        String normalized = value.trim();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Email has an invalid format");
        }
        return new Email(normalized);
    }

    /**
     * Gets the normalized email text.
     *
     * @return Email text.
     */
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return value;
    }
}
