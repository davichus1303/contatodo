package com.contatodo.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link Email} value object.
 */
class EmailTest {

    @Test
    void ofAcceptsValidEmailAndTrimsInput() {
        Email email = Email.of("  user@example.com  ");
        assertEquals("user@example.com", email.getValue());
    }

    @Test
    void ofRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> Email.of(null));
    }

    @Test
    void ofRejectsBlank() {
        assertThrows(IllegalArgumentException.class, () -> Email.of("   "));
    }

    @Test
    void ofRejectsMissingAtSign() {
        assertThrows(IllegalArgumentException.class, () -> Email.of("user.example.com"));
    }

    @Test
    void ofRejectsMissingDomainSuffix() {
        assertThrows(IllegalArgumentException.class, () -> Email.of("user@example"));
    }
}
