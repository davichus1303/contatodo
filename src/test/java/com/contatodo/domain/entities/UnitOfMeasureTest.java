package com.contatodo.domain.entities;

import com.contatodo.domain.exception.InvalidEntityStateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link UnitOfMeasure} invariants and transitions.
 */
class UnitOfMeasureTest {

    private UnitOfMeasure validUnitOfMeasure() {
        return UnitOfMeasure.builder()
                .id("u-1")
                .name("Kilogram")
                .abrev("kg")
                .companyOid("company-1")
                .isActive(true)
                .isDeleted(false)
                .build();
    }

    @Test
    void buildsWithAllAttributes() {
        UnitOfMeasure unitOfMeasure = validUnitOfMeasure();

        assertEquals("u-1", unitOfMeasure.getId());
        assertEquals("Kilogram", unitOfMeasure.getName());
        assertEquals("kg", unitOfMeasure.getAbrev());
        assertEquals("company-1", unitOfMeasure.getCompanyOid());
        assertTrue(unitOfMeasure.getIsActive());
        assertFalse(unitOfMeasure.getIsDeleted());
    }

    @Test
    void rejectsMissingName() {
        assertThrows(InvalidEntityStateException.class, () -> UnitOfMeasure.builder()
                .name(" ")
                .abrev("kg")
                .build());
    }

    @Test
    void rejectsMissingAbbreviation() {
        assertThrows(InvalidEntityStateException.class, () -> UnitOfMeasure.builder()
                .name("Kilogram")
                .abrev(null)
                .build());
    }

    @Test
    void allowsACompanyLessUnitOfMeasure() {
        UnitOfMeasure unitOfMeasure = UnitOfMeasure.builder()
                .name("Kilogram")
                .abrev("kg")
                .build();

        assertNull(unitOfMeasure.getCompanyOid());
    }

    @Test
    void markDeletedSetsBothFlagsAndKeepsTheRest() {
        UnitOfMeasure deleted = validUnitOfMeasure().markDeleted();

        assertEquals("u-1", deleted.getId());
        assertEquals("Kilogram", deleted.getName());
        assertEquals("kg", deleted.getAbrev());
        assertEquals("company-1", deleted.getCompanyOid());
        assertFalse(deleted.getIsActive());
        assertTrue(deleted.getIsDeleted());
    }
}
