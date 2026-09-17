package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.CompanyDocument;
import com.contatodo.domain.entities.Company;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link CompanyPersistenceMapper}.
 */
class CompanyPersistenceMapperTest {

    private final CompanyPersistenceMapper persistenceMapper = new CompanyPersistenceMapper();

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
                .updatedDate(LocalDateTime.of(2026, 1, 2, 0, 0))
                .createdBy("creator-1")
                .build();
    }

    @Test
    void toDocumentCopiesEveryField() {
        CompanyDocument document = persistenceMapper.toDocument(company());

        assertEquals("company-1", document.getId());
        assertEquals("Acme", document.getName());
        assertEquals("ACM010101ABC", document.getRfc());
        assertEquals("https://acme.example.com", document.getWebSite());
        assertEquals("Lima", document.getUbication());
        assertEquals("user-1", document.getContactUserOId());
        assertEquals(Boolean.TRUE, document.getIsActive());
        assertEquals(Boolean.FALSE, document.getIsDeleted());
        assertEquals(LocalDateTime.of(2026, 1, 1, 0, 0), document.getCreatedDate());
        assertEquals(LocalDateTime.of(2026, 1, 2, 0, 0), document.getUpdatedDate());
        assertEquals("creator-1", document.getCreatedBy());
    }

    @Test
    void roundTripPreservesEveryField() {
        Company reloaded = persistenceMapper.toEntity(persistenceMapper.toDocument(company()));

        assertEquals("company-1", reloaded.getId());
        assertEquals("Acme", reloaded.getName());
        assertEquals("ACM010101ABC", reloaded.getRfc());
        assertEquals("https://acme.example.com", reloaded.getWebSite());
        assertEquals("Lima", reloaded.getUbication());
        assertEquals("user-1", reloaded.getContactUserOId());
        assertEquals(Boolean.TRUE, reloaded.getIsActive());
        assertEquals(Boolean.FALSE, reloaded.getIsDeleted());
        assertEquals("creator-1", reloaded.getCreatedBy());
    }
}
