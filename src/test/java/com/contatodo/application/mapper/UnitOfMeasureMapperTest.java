package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateUnitOfMeasureRequest;
import com.contatodo.application.dto.response.UnitOfMeasureResponse;
import com.contatodo.domain.entities.UnitOfMeasure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link UnitOfMeasureMapper}.
 */
class UnitOfMeasureMapperTest {

    private final UnitOfMeasureMapper mapper = new UnitOfMeasureMapper();

    @Test
    void toEntityMapsTheCreateRequestAndActivatesTheUnit() {
        CreateUnitOfMeasureRequest request = new CreateUnitOfMeasureRequest();
        request.setName("Kilogram");
        request.setAbrev("kg");

        UnitOfMeasure unitOfMeasure = mapper.toEntity(request, "company-1");

        assertNull(unitOfMeasure.getId());
        assertEquals("Kilogram", unitOfMeasure.getName());
        assertEquals("kg", unitOfMeasure.getAbrev());
        assertEquals("company-1", unitOfMeasure.getCompanyOid());
        assertTrue(unitOfMeasure.getIsActive());
        assertFalse(unitOfMeasure.getIsDeleted());
    }

    @Test
    void toResponseCopiesEveryField() {
        UnitOfMeasure unitOfMeasure = UnitOfMeasure.builder()
                .id("u-1")
                .name("Kilogram")
                .abrev("kg")
                .companyOid("company-1")
                .isActive(true)
                .isDeleted(false)
                .build();

        UnitOfMeasureResponse response = mapper.toResponse(unitOfMeasure);

        assertEquals("u-1", response.getId());
        assertEquals("Kilogram", response.getName());
        assertEquals("kg", response.getAbrev());
        assertEquals("company-1", response.getCompanyOid());
        assertTrue(response.getIsActive());
        assertFalse(response.getIsDeleted());
    }
}
