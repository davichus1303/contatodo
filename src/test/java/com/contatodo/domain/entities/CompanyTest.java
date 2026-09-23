package com.contatodo.domain.entities;

import com.contatodo.domain.exception.InvalidEntityStateException;
import com.contatodo.shared.constants.ValidationConstants;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Company} entity invariants.
 */
class CompanyTest {

    private Company company() {
        return Company.builder()
                .id("company-1")
                .name("Acme")
                .rfc("ACM010101ABC")
                .webSite("https://acme.example.com")
                .ubication("Lima")
                .contactUserOId("user-1")
                .isActive(true)
                .isDeleted(false)
                .createdDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .updatedDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .createdBy("creator-1")
                .build();
    }

    @Test
    void buildKeepsEveryProvidedField() {
        Company company = company();

        assertEquals("company-1", company.getId());
        assertEquals("Acme", company.getName());
        assertEquals("ACM010101ABC", company.getRfc());
        assertEquals("https://acme.example.com", company.getWebSite());
        assertEquals("Lima", company.getUbication());
        assertEquals("user-1", company.getContactUserOId());
        assertEquals(Boolean.TRUE, company.getIsActive());
        assertEquals(Boolean.FALSE, company.getIsDeleted());
        assertEquals("creator-1", company.getCreatedBy());
    }

    @Test
    void buildRejectsMissingName() {
        InvalidEntityStateException exception = assertThrows(
                InvalidEntityStateException.class,
                () -> Company.builder().name(null).build()
        );

        assertEquals(ValidationConstants.FIELD_REQUIRED, exception.getMessage());
    }

    @Test
    void buildRejectsBlankName() {
        assertThrows(InvalidEntityStateException.class, () -> Company.builder().name("   ").build());
    }

    @Test
    void markDeletedFlagsAsDeletedAndInactive() {
        Company deleted = company().markDeleted();

        assertEquals("company-1", deleted.getId());
        assertEquals(Boolean.TRUE, deleted.getIsDeleted());
        assertEquals(Boolean.FALSE, deleted.getIsActive());
    }

    @Test
    void markDeletedRefreshesUpdatedDateKeepingCreatedDate() {
        Company original = company();

        Company deleted = original.markDeleted();

        assertEquals(original.getCreatedDate(), deleted.getCreatedDate());
        assertTrue(deleted.getUpdatedDate().isAfter(original.getUpdatedDate()));
    }
}
