package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.UnitOfMeasureDocument;
import com.contatodo.domain.entities.UnitOfMeasure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link UnitOfMeasurePersistenceMapper}.
 */
class UnitOfMeasurePersistenceMapperTest {

    private final UnitOfMeasurePersistenceMapper mapper = new UnitOfMeasurePersistenceMapper();

    @Test
    void toDocumentCopiesEveryField() {
        UnitOfMeasure unitOfMeasure = UnitOfMeasure.builder()
                .id("u-1")
                .name("Kilogram")
                .abrev("kg")
                .companyOid("company-1")
                .isActive(true)
                .isDeleted(false)
                .build();

        UnitOfMeasureDocument document = mapper.toDocument(unitOfMeasure);

        assertEquals("u-1", document.getId());
        assertEquals("Kilogram", document.getName());
        assertEquals("kg", document.getAbrev());
        assertEquals("company-1", document.getCompanyOid());
        assertTrue(document.getIsActive());
        assertFalse(document.getIsDeleted());
    }

    @Test
    void toEntityCopiesEveryField() {
        UnitOfMeasureDocument document = new UnitOfMeasureDocument();
        document.setId("u-1");
        document.setName("Kilogram");
        document.setAbrev("kg");
        document.setCompanyOid("company-1");
        document.setIsActive(true);
        document.setIsDeleted(false);

        UnitOfMeasure unitOfMeasure = mapper.toEntity(document);

        assertEquals("u-1", unitOfMeasure.getId());
        assertEquals("Kilogram", unitOfMeasure.getName());
        assertEquals("kg", unitOfMeasure.getAbrev());
        assertEquals("company-1", unitOfMeasure.getCompanyOid());
        assertTrue(unitOfMeasure.getIsActive());
        assertFalse(unitOfMeasure.getIsDeleted());
    }
}
