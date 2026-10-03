package com.contatodo.infrastructure.security;

import com.contatodo.application.port.TokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
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

    @Test
    @SuppressWarnings("unchecked")
    void getClaimValueReadsTheStructuredPermissionClaimOfTheCurrentRequest() {
        List<Map<String, Object>> permissions = List.of(Map.of(
                "moduleOid", "users-oid",
                "permissions", Map.of("view", true, "create", false, "update", true, "delete", false)
        ));
        Map<String, Object> claims = new HashMap<>();
        claims.put("permissionOfRole", permissions);
        String token = jwtService.generateToken("user@example.com", claims);
        bindTokenToCurrentRequest(token);

        Object claim = jwtService.getClaimValue("permissionOfRole");

        assertInstanceOf(List.class, claim);
        List<Object> entries = (List<Object>) claim;
        assertEquals(1, entries.size());
        Map<String, Object> entry = (Map<String, Object>) entries.get(0);
        assertEquals("users-oid", entry.get("moduleOid"));
        assertEquals(
                Map.of("view", true, "create", false, "update", true, "delete", false),
                entry.get("permissions")
        );
    }

    @Test
    void getClaimValueReturnsNullWithoutAnAuthorizationHeader() {
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest())
        );

        assertNull(jwtService.getClaimValue("permissionOfRole"));
    }

    private void bindTokenToCurrentRequest(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
