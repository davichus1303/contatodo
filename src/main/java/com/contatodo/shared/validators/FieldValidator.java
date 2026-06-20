package com.contatodo.shared.validators;

import com.contatodo.shared.constants.ValidationConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reusable validator for common field validations.
 */
@Component
public class FieldValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Validates that a string field is not null or empty.
     *
     * @param value Field value.
     * @param fieldName Field name for error messages.
     * @param errors Error list to append to.
     */
    public void validateRequired(String value, String fieldName, List<String> errors) {
        if (value == null) {
            errors.add(fieldName + ": " + ValidationConstants.FIELD_NULL);
            return;
        }
        if (value.trim().isEmpty()) {
            errors.add(fieldName + ": " + ValidationConstants.FIELD_EMPTY);
        }
    }

    /**
     * Validates email format.
     *
     * @param email Email value.
     * @param fieldName Field name for error messages.
     * @param errors Error list to append to.
     */
    public void validateEmail(String email, String fieldName, List<String> errors) {
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.add(fieldName + ": " + ValidationConstants.FIELD_INVALID_EMAIL);
        }
    }

    /**
     * Validates string length.
     *
     * @param value Field value.
     * @param fieldName Field name for error messages.
     * @param minLength Minimum allowed length.
     * @param maxLength Maximum allowed length.
     * @param errors Error list to append to.
     */
    public void validateLength(String value, String fieldName, int minLength, int maxLength, List<String> errors) {
        if (value == null) {
            return;
        }
        int length = value.trim().length();
        if (length < minLength || length > maxLength) {
            errors.add(fieldName + ": " + ValidationConstants.FIELD_INVALID_LENGTH);
        }
    }

    /**
     * Validates that a numeric value is not null and is zero or greater.
     *
     * @param value Numeric value.
     * @param fieldName Field name for error messages.
     * @param errors Error list to append to.
     */
    public void validateNonNegative(Number value, String fieldName, List<String> errors) {
        if (value == null) {
            errors.add(fieldName + ": " + ValidationConstants.FIELD_NULL);
            return;
        }
        if (value.doubleValue() < 0) {
            errors.add(fieldName + ": " + ValidationConstants.FIELD_INVALID_TYPE);
        }
    }

    /**
     * Creates a new mutable error list.
     *
     * @return Empty error list.
     */
    public List<String> createErrorList() {
        return new ArrayList<>();
    }
}
