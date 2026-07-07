package com.contatodo.shared.constants;

/**
 * Constants for field validation messages.
 */
public final class ValidationConstants {

    public static final String FIELD_REQUIRED = "Field is required.";
    public static final String FIELD_INVALID_TYPE = "Field has an invalid type.";
    public static final String FIELD_INVALID_LENGTH = "Field has an invalid length.";
    public static final String FIELD_NULL = "Field cannot be null.";
    public static final String FIELD_EMPTY = "Field cannot be empty.";
    public static final String FIELD_INVALID_EMAIL = "Field must be a valid email address.";
    public static final String FIELD_INVALID_PASSWORD = "Password must contain at least one uppercase letter, one symbol, and be at least 8 characters long.";

    private ValidationConstants() {
    }
}
