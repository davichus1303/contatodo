package com.contatodo.shared.constants;

/**
 * Constants for authentication module messages.
 */
public final class AuthConstants {

    public static final String LOGIN_SUCCESS = "Login successful.";
    public static final String TOKEN_INVALID = "Invalid or expired token.";
    public static final String TOKEN_MISSING = "Authorization token is missing.";
    public static final String ACCESS_DENIED = "Access denied.";
    public static final String USER_NOT_AUTHENTICATED = "User is not authenticated.";
    public static final String COMPANY_CONTEXT_REQUIRED = "Company context is required.";

    public static final String JWT_CLAIM_COMPANY_OID = "companyOid";
    public static final String JWT_CLAIM_ROLE = "role";
    public static final String ROOT_ROLE_CLAIM = "ROOT";
    public static final String ROOT_ROLE_NAME = "Root";

    private AuthConstants() {
    }
}
