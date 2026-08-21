package com.contatodo.application.port;

/**
 * Outbound port for security token generation.
 *
 * <p>Isolates the application layer from the concrete token technology
 * (JWT) living in infrastructure.</p>
 */
public interface TokenProvider {

    /**
     * Generates a signed token for the given subject.
     *
     * @param subject Token subject (user email).
     * @return Signed token.
     */
    String generateToken(String subject);
}
