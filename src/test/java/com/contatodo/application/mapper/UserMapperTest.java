package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.UpdateUserRequest;
import com.contatodo.domain.entities.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link UserMapper#applyUpdate(User, UpdateUserRequest, String)}.
 */
class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    private User existingUser() {
        return User.builder()
                .id("user-1")
                .userName("david")
                .email("david@example.com")
                .password("hashed")
                .name("David")
                .roleId("role-1")
                .createdByUserOid("creator-1")
                .isActive(true)
                .isDelete(false)
                .createdDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .updatedDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .build();
    }

    @Test
    void applyUpdateReplacesTheRoleWhenARequestRoleIsProvided() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setRoleId("role-2");

        User updated = userMapper.applyUpdate(existingUser(), request, null);

        assertEquals("role-2", updated.getRoleId());
    }

    @Test
    void applyUpdateKeepsTheCurrentRoleWhenTheRequestRoleIsNull() {
        UpdateUserRequest request = new UpdateUserRequest();

        User updated = userMapper.applyUpdate(existingUser(), request, null);

        assertEquals("role-1", updated.getRoleId());
    }

    @Test
    void applyUpdateKeepsTheCurrentPasswordWhenNoHashedPasswordIsProvided() {
        UpdateUserRequest request = new UpdateUserRequest();

        User updated = userMapper.applyUpdate(existingUser(), request, null);

        assertEquals("hashed", updated.getPassword());
    }

    @Test
    void applyUpdateAppliesEveryProvidedField() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUserName("david-new");
        request.setEmail("david-new@example.com");
        request.setName("David New");
        request.setIsActive(false);

        User updated = userMapper.applyUpdate(existingUser(), request, "new-hash");

        assertEquals("david-new", updated.getUserName());
        assertEquals("david-new@example.com", updated.getEmail());
        assertEquals("David New", updated.getName());
        assertEquals("new-hash", updated.getPassword());
        assertEquals(false, updated.isActive());
        assertEquals("role-1", updated.getRoleId());
    }
}
