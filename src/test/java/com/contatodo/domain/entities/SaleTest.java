package com.contatodo.domain.entities;

import com.contatodo.shared.constants.SaleConstants;
import com.contatodo.shared.exceptions.SaleWithoutProfitException;
import com.contatodo.domain.model.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link Sale} factory invariants.
 */
class SaleTest {

    @Test
    void placeCreatesProfitableSale() {
        Sale sale = Sale.place(
                1L, "product-1", "Cafe", "user-1",
                2, 40.0, Money.of(20.0), Money.of(30.0), "first sale"
        );

        assertEquals(1L, sale.getSaleNumber());
        assertEquals("Cafe", sale.getProductName());
        assertEquals(2, sale.getQuantity());
        assertEquals(40.0, sale.getTotalSalePrice());
        assertEquals(20.0, sale.getTotalCost());
        assertEquals(30.0, sale.getOriginalTotalPrice());
        assertEquals(Boolean.FALSE, sale.getIsDeleted());
    }

    @Test
    void placeRejectsBreakEvenSale() {
        SaleWithoutProfitException exception = assertThrows(
                SaleWithoutProfitException.class,
                () -> Sale.place(
                        1L, "product-1", "Cafe", "user-1",
                        2, 20.0, Money.of(20.0), Money.of(30.0), null
                )
        );
        assertEquals(SaleConstants.SALE_WITHOUT_PROFIT, exception.getMessage());
    }

    @Test
    void placeRejectsLossSale() {
        assertThrows(
                SaleWithoutProfitException.class,
                () -> Sale.place(
                        1L, "product-1", "Cafe", "user-1",
                        2, 15.0, Money.of(20.0), Money.of(30.0), null
                )
        );
    }

    @Test
    void builderRejectsMissingProduct() {
        assertThrows(com.contatodo.domain.exception.InvalidEntityStateException.class,
                () -> Sale.builder().quantity(1).userOid("u").build());
    }

    @Test
    void builderRejectsNonPositiveQuantity() {
        assertThrows(com.contatodo.domain.exception.InvalidEntityStateException.class,
                () -> Sale.builder().productOid("p").quantity(0).userOid("u").build());
    }
}
