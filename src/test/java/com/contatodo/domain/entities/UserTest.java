package com.contatodo.domain.entities;

import com.contatodo.shared.constants.UserConstants;
import com.contatodo.domain.exception.InvalidEntityStateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link User} entity invariants.
 */
class UserTest {

    private User activeUser() {
        return User.builder()
                .userName("david")
                .email("david@example.com")
                .password("secret")
                .name("David")
                .build();
    }

    @Test
    void buildCreatesActiveUser() {
        User user = activeUser();
        assertTrue(user.isActive());
        assertFalse(user.isDelete());
        assertEquals("david@example.com", user.getEmail());
    }

    @Test
    void buildRejectsMissingUserName() {
        assertThrows(InvalidEntityStateException.class,
                () -> User.builder().email("a@b.com").password("x").name("N").build());
    }

    @Test
    void buildRejectsInvalidEmailFormat() {
        assertThrows(Exception.class,
                () -> User.builder().userName("u").email("not-an-email").password("x").name("N").build());
    }

    @Test
    void markDeletedDeactivatesAndFlagsUser() {
        User deleted = activeUser().markDeleted();
        assertFalse(deleted.isActive());
        assertTrue(deleted.isDelete());
    }

    @Test
    void builderRequiresPassword() {
        InvalidEntityStateException exception = assertThrows(
                InvalidEntityStateException.class,
                () -> User.builder().userName("u").email("a@b.com").name("N").build()
        );
        assertEquals(UserConstants.USER_PASSWORD_REQUIRED, exception.getMessage());
    }
}
