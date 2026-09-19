package com.contatodo.application.port;

import java.util.Map;

/**
 * Outbound port for security token generation.
 *
 * <p>Isolates the application layer from the concrete token technology
 * (JWT) living in infrastructure.</p>
 */
public interface TokenProvider {

    /**
     * Generates a signed token for the given subject with optional claims.
     *
     * @param subject Token subject (user email).
     * @param claims Additional claims to include in the token (optional).
     * @return Signed token.
     */
    String generateToken(String subject, Map<String, Object> claims);

    /**
     * Generates a signed token for the given subject without additional claims.
     *
     * @param subject Token subject (user email).
     * @return Signed token.
     */
    default String generateToken(String subject) {
        return generateToken(subject, Map.of());
    }
}