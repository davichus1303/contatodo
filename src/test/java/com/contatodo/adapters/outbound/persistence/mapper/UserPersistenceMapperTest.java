package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.UserDocument;
import com.contatodo.domain.entities.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link UserPersistenceMapper}.
 */
class UserPersistenceMapperTest {

    private final UserPersistenceMapper mapper = new UserPersistenceMapper();

    @Test
    void toDocumentMapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id("user-1")
                .userName("jdoe")
                .email("john@example.com")
                .password("hashed")
                .name("John Doe")
                .phoneNumber("+1234567890")
                .roleId("role-1")
                .createdByUserOid("creator-1")
                .isActive(true)
                .isDelete(false)
                .createdDate(now)
                .updatedDate(now)
                .createdBy("creator-1")
                .updatedBy("updater-1")
                .build();

        UserDocument document = mapper.toDocument(user);

        assertEquals("user-1", document.getId());
        assertEquals("jdoe", document.getUserName());
        assertEquals("john@example.com", document.getEmail());
        assertEquals("hashed", document.getPassword());
        assertEquals("John Doe", document.getName());
        assertEquals("+1234567890", document.getPhoneNumber());
        assertEquals("role-1", document.getRoleId());
        assertEquals("creator-1", document.getCreatedByUserOid());
        assertEquals(true, document.isActive());
        assertEquals(false, document.getIsDeleted());
        assertEquals(now, document.getCreatedDate());
        assertEquals(now, document.getUpdatedDate());
        assertEquals("creator-1", document.getCreatedBy());
        assertEquals("updater-1", document.getUpdatedBy());
    }

    @Test
    void toEntityMapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        UserDocument document = new UserDocument();
        document.setId("user-1");
        document.setUserName("jdoe");
        document.setEmail("john@example.com");
        document.setPassword("hashed");
        document.setName("John Doe");
        document.setPhoneNumber("+1234567890");
        document.setRoleId("role-1");
        document.setCreatedByUserOid("creator-1");
        document.setActive(true);
        document.setIsDeleted(false);
        document.setCreatedDate(now);
        document.setUpdatedDate(now);
        document.setCreatedBy("creator-1");
        document.setUpdatedBy("updater-1");

        User user = mapper.toEntity(document);

        assertEquals("user-1", user.getId());
        assertEquals("jdoe", user.getUserName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("hashed", user.getPassword());
        assertEquals("John Doe", user.getName());
        assertEquals("+1234567890", user.getPhoneNumber());
        assertEquals("role-1", user.getRoleId());
        assertEquals("creator-1", user.getCreatedByUserOid());
        assertEquals(true, user.isActive());
        assertEquals(false, user.isDelete());
        assertEquals(now, user.getCreatedDate());
        assertEquals(now, user.getUpdatedDate());
        assertEquals("creator-1", user.getCreatedBy());
        assertEquals("updater-1", user.getUpdatedBy());
    }

    @Test
    void toEntityHandlesNullPhoneNumber() {
        UserDocument document = new UserDocument();
        document.setId("user-1");
        document.setUserName("jdoe");
        document.setEmail("john@example.com");
        document.setPassword("hashed");
        document.setName("John Doe");
        document.setPhoneNumber(null);
        document.setRoleId("role-1");
        document.setCreatedByUserOid("creator-1");
        document.setActive(true);
        document.setIsDeleted(false);
        document.setCreatedDate(LocalDateTime.now());
        document.setUpdatedDate(LocalDateTime.now());

        User user = mapper.toEntity(document);

        assertNull(user.getPhoneNumber());
    }

    @Test
    void roundTripPreservesData() {
        LocalDateTime now = LocalDateTime.now();
        User original = User.builder()
                .id("user-1")
                .userName("jdoe")
                .email("john@example.com")
                .password("hashed")
                .name("John Doe")
                .phoneNumber("+1234567890")
                .roleId("role-1")
                .createdByUserOid("creator-1")
                .isActive(true)
                .isDelete(false)
                .createdDate(now)
                .updatedDate(now)
                .createdBy("creator-1")
                .updatedBy("updater-1")
                .build();

        UserDocument document = mapper.toDocument(original);
        User roundTripped = mapper.toEntity(document);

        assertEquals(original.getId(), roundTripped.getId());
        assertEquals(original.getUserName(), roundTripped.getUserName());
        assertEquals(original.getEmail(), roundTripped.getEmail());
        assertEquals(original.getPassword(), roundTripped.getPassword());
        assertEquals(original.getName(), roundTripped.getName());
        assertEquals(original.getPhoneNumber(), roundTripped.getPhoneNumber());
        assertEquals(original.getRoleId(), roundTripped.getRoleId());
        assertEquals(original.getCreatedByUserOid(), roundTripped.getCreatedByUserOid());
        assertEquals(original.isActive(), roundTripped.isActive());
        assertEquals(original.isDelete(), roundTripped.isDelete());
    }
}