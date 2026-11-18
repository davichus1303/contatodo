package com.contatodo.application.mapper;

import com.contatodo.application.dto.response.SaleResponse;
import com.contatodo.domain.entities.Sale;
import com.contatodo.domain.entities.UnitOfMeasure;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link SaleMapper} unit of measure joining.
 */
class SaleMapperTest {

    private final SaleMapper mapper = new SaleMapper();

    private Sale sale(String unitOfMeasureOid) {
        return Sale.builder()
                .id("sale-1")
                .saleNumber(1L)
                .productOid("product-1")
                .productName("Cafe")
                .byUserOid("user-1")
                .quantity(2)
                .unitOfMeasureOid(unitOfMeasureOid)
                .totalCost(20.0)
                .originalTotalPrice(30.0)
                .totalSalePrice(30.0)
                .build();
    }

    private UnitOfMeasure unit(String id, String name, String abrev) {
        return UnitOfMeasure.builder()
                .id(id)
                .name(name)
                .abrev(abrev)
                .companyOid("company-1")
                .isActive(true)
                .isDeleted(false)
                .build();
    }

    @Test
    void joinsTheNameAndAbbreviationOfTheSaleUnit() {
        SaleResponse response = mapper.toResponseWithUnitOfMeasureDetails(
                sale("uom-1"),
                Map.of("uom-1", unit("uom-1", "Kilogramo", "kg"))
        );

        assertEquals("Kilogramo", response.getUnitOfMeasureName());
        assertEquals("kg", response.getUnitOfMeasureAbbrev());
    }

    @Test
    void keepsTheUnitFieldsNullWhenTheUnitIsNotInTheCatalog() {
        SaleResponse response = mapper.toResponseWithUnitOfMeasureDetails(
                sale("uom-missing"),
                Map.of("uom-1", unit("uom-1", "Kilogramo", "kg"))
        );

        assertNull(response.getUnitOfMeasureName());
        assertNull(response.getUnitOfMeasureAbbrev());
    }

    @Test
    void keepsTheUnitFieldsNullForLegacySalesWithoutUnit() {
        SaleResponse response = mapper.toResponseWithUnitOfMeasureDetails(sale(null), Map.of());

        assertNull(response.getUnitOfMeasureName());
        assertNull(response.getUnitOfMeasureAbbrev());
    }

    @Test
    void joinsTheUnitDetailsOfEverySale() {
        List<SaleResponse> responses = mapper.toResponseListWithUnitOfMeasureDetails(
                List.of(sale("uom-1"), sale("uom-2")),
                Map.of(
                        "uom-1", unit("uom-1", "Kilogramo", "kg"),
                        "uom-2", unit("uom-2", "Unidad", "u")
                )
        );

        assertEquals("kg", responses.get(0).getUnitOfMeasureAbbrev());
        assertEquals("u", responses.get(1).getUnitOfMeasureAbbrev());
    }
}
