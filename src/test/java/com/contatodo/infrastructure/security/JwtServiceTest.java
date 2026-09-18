package com.contatodo.infrastructure.security;

import com.contatodo.application.port.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link JwtService}.
 */
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("test-secret-key-with-sufficient-length-32chars", 3600000);
    }

    @Test
    void generateTokenReturnsNonEmptyString() {
        String token = jwtService.generateToken("user@example.com");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractSubjectReturnsOriginalSubject() {
        String subject = "user@example.com";
        String token = jwtService.generateToken(subject);

        String extracted = jwtService.extractSubject(token);

        assertEquals(subject, extracted);
    }

    @Test
    void isTokenValidReturnsTrueForValidToken() {
        String token = jwtService.generateToken("user@example.com");

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValidReturnsFalseForMalformedToken() {
        assertFalse(jwtService.isTokenValid("invalid.token.string"));
    }

    @Test
    void isTokenValidReturnsFalseForNullToken() {
        assertFalse(jwtService.isTokenValid(null));
    }

    @Test
    void isTokenValidReturnsFalseForEmptyToken() {
        assertFalse(jwtService.isTokenValid(""));
    }

    @Test
    void generateTokenProducesDifferentTokensForSameSubject() {
        String token1 = jwtService.generateToken("user@example.com");
        String token2 = jwtService.generateToken("user@example.com");

        assertEquals(jwtService.extractSubject(token1), jwtService.extractSubject(token2));
    }

    @Test
    void differentSecretsProduceIncompatibleTokens() {
        JwtService otherService = new JwtService("different-secret-key-with-sufficient-length", 3600000);
        String token = jwtService.generateToken("user@example.com");

        assertFalse(otherService.isTokenValid(token));
    }
}