package com.contatodo.domain.entities;

import com.contatodo.shared.constants.ProductConstants;
import com.contatodo.shared.constants.SaleConstants;
import com.contatodo.shared.exceptions.InsufficientStockException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link Product} aggregate invariants.
 */
class ProductTest {

    private Product productWithStock(int stock) {
        return Product.builder()
                .name("Cafe")
                .stock(stock)
                .code("1")
                .realCost(10.0)
                .unitRealCost(10.0)
                .unitPublicCost(15.0)
                .isActive(true)
                .userOid("user-1")
                .build();
    }

    @Test
    void buildRejectsMissingName() {
        Exception exception = assertThrows(
                com.contatodo.domain.exception.InvalidEntityStateException.class,
                () -> Product.builder().stock(1).build()
        );
        assertEquals(ProductConstants.PRODUCT_NAME_REQUIRED, exception.getMessage());
    }

    @Test
    void decreaseStockSubtractsRequestedQuantity() {
        Product updated = productWithStock(10).decreaseStock(4);
        assertEquals(6, updated.getStock());
    }

    @Test
    void decreaseStockRejectsZeroStock() {
        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> productWithStock(0).decreaseStock(1)
        );
        assertEquals(SaleConstants.PRODUCT_OUT_OF_STOCK, exception.getMessage());
    }

    @Test
    void decreaseStockRejectsQuantityAboveStock() {
        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> productWithStock(3).decreaseStock(5)
        );
        assertEquals(SaleConstants.INSUFFICIENT_STOCK, exception.getMessage());
    }

    @Test
    void replenishAddsAcquiredQuantity() {
        Product updated = productWithStock(7).replenish(5);
        assertEquals(12, updated.getStock());
    }

    @Test
    void replenishTreatsNullStockAsZero() {
        Product base = Product.builder()
                .name("Cafe")
                .stock(null)
                .code("2")
                .realCost(1.0)
                .userOid("user-1")
                .build();
        assertEquals(5, base.replenish(5).getStock());
    }

    @Test
    void withAverageUnitRealCostKeepsTotalConsistent() {
        Product updated = productWithStock(4).withAverageUnitRealCost(12.5);
        assertEquals(12.5, updated.getUnitRealCost());
        assertEquals(50.0, updated.getRealCost());
    }

    @Test
    void buildRejectsNegativeStock() {
        Exception exception = assertThrows(
                com.contatodo.domain.exception.InvalidEntityStateException.class,
                () -> Product.builder().name("Cafe").stock(-1).build()
        );
        assertEquals(ProductConstants.PRODUCT_STOCK_INVALID, exception.getMessage());
    }
}
