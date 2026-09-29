package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateUserRequest;
import com.contatodo.application.dto.request.UpdateUserRequest;
import com.contatodo.application.dto.response.UserResponse;
import com.contatodo.domain.entities.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link UserMapper}.
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
                .phoneNumber("987654321")
                .roleId("role-1")
                .byUserOid("creator-1")
                .isActive(true)
                .isDelete(false)
                .createdDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .updatedDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .build();
    }

    @Test
    void toEntityCopiesThePhoneNumberFromTheCreateRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("david");
        request.setEmail("david@example.com");
        request.setName("David");
        request.setPhoneNumber("987654321");

        User user = userMapper.toEntity(request, "hashed", "role-1", "creator-1", true);

        assertEquals("987654321", user.getPhoneNumber());
    }

    @Test
    void toEntityLeavesThePhoneNumberNullWhenItIsNotProvided() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("david");
        request.setEmail("david@example.com");
        request.setName("David");

        User user = userMapper.toEntity(request, "hashed", "role-1", "creator-1", true);

        assertNull(user.getPhoneNumber());
    }

    @Test
    void toResponseCopiesThePhoneNumber() {
        UserResponse response = userMapper.toResponse(existingUser());

        assertEquals("987654321", response.getPhoneNumber());
    }

    @Test
    void applyUpdateReplacesTheRoleWhenARequestRoleIsProvided() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setRoleId("role-2");

        User updated = userMapper.applyUpdate(existingUser(), request, null, "editor-1");

        assertEquals("role-2", updated.getRoleId());
    }

    @Test
    void applyUpdateKeepsTheCurrentRoleWhenTheRequestRoleIsNull() {
        UpdateUserRequest request = new UpdateUserRequest();

        User updated = userMapper.applyUpdate(existingUser(), request, null, "editor-1");

        assertEquals("role-1", updated.getRoleId());
    }

    @Test
    void applyUpdateKeepsTheCurrentPasswordWhenNoHashedPasswordIsProvided() {
        UpdateUserRequest request = new UpdateUserRequest();

        User updated = userMapper.applyUpdate(existingUser(), request, null, "editor-1");

        assertEquals("hashed", updated.getPassword());
    }

    @Test
    void applyUpdateKeepsTheCurrentPhoneNumberWhenTheRequestPhoneNumberIsNull() {
        UpdateUserRequest request = new UpdateUserRequest();

        User updated = userMapper.applyUpdate(existingUser(), request, null, "editor-1");

        assertEquals("987654321", updated.getPhoneNumber());
    }

    @Test
    void applyUpdateAppliesEveryProvidedField() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUserName("david-new");
        request.setEmail("david-new@example.com");
        request.setName("David New");
        request.setPhoneNumber("999888777");
        request.setIsActive(false);

        User updated = userMapper.applyUpdate(existingUser(), request, "new-hash", "editor-1");

        assertEquals("david-new", updated.getUserName());
        assertEquals("david-new@example.com", updated.getEmail());
        assertEquals("David New", updated.getName());
        assertEquals("999888777", updated.getPhoneNumber());
        assertEquals("new-hash", updated.getPassword());
        assertEquals(false, updated.isActive());
        assertEquals("role-1", updated.getRoleId());
    }

    @Test
    void toEntityRecordsTheCreatorFromTheProvidedParameter() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("david");
        request.setEmail("david@example.com");
        request.setName("David");

        User user = userMapper.toEntity(request, "hashed", "role-1", "creator-1", true);

        assertEquals("creator-1", user.getByUserOid());
    }

    @Test
    void toEntityUsesTheResolvedRoleAndNotTheOneSentInTheRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("david");
        request.setEmail("david@example.com");
        request.setName("David");
        request.setRoleId("role-root");

        User user = userMapper.toEntity(request, "hashed", null, null, false);

        assertNull(user.getRoleId());
    }

    @Test
    void applyUpdateKeepsTheOriginalCreatorAndRecordsTheEditor() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("David New");

        User updated = userMapper.applyUpdate(existingUser(), request, null, "editor-9");

        assertEquals("creator-1", updated.getByUserOid());
        assertEquals("editor-9", updated.getUpdatedByUserOid());
    }

    @Test
    void applyUpdateOverwritesThePreviousEditorWithTheCurrentOne() {
        User previouslyEdited = User.builder()
                .id("user-1")
                .userName("david")
                .email("david@example.com")
                .password("hashed")
                .name("David")
                .byUserOid("creator-1")
                .updatedByUserOid("editor-1")
                .createdDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .updatedDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .build();

        User updated = userMapper.applyUpdate(previouslyEdited, new UpdateUserRequest(), null, "editor-2");

        assertEquals("editor-2", updated.getUpdatedByUserOid());
    }

    @Test
    void createRequestCarriesNoAuthorFieldSoTheClientCannotForgeIt() {
        assertEquals(
                0,
                java.util.Arrays.stream(CreateUserRequest.class.getMethods())
                        .filter(method -> method.getName().toLowerCase().contains("byuseroid"))
                        .count()
        );
    }
}
