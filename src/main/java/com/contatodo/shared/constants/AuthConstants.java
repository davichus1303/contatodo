package com.contatodo.shared.constants;

/**
 * Constants for authentication module messages.
 */
public final class AuthConstants {

    public static final String LOGIN_SUCCESS = "Login successful.";
    public static final String TOKEN_INVALID = "Invalid or expired token.";
    public static final String TOKEN_MISSING = "Authorization token is missing.";
    public static final String ACCESS_DENIED = "Access denied.";

    private AuthConstants() {
    }
}
