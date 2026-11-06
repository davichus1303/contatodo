package com.contatodo.domain.entities;

import com.contatodo.shared.constants.ProductConstants;
import com.contatodo.shared.constants.SaleConstants;
import com.contatodo.shared.exceptions.InsufficientStockException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                .byUserOid("user-1")
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
                .byUserOid("user-1")
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

    @Test
    void buildDefaultsUnitOfMeasuresToAnEmptyList() {
        Product product = Product.builder().name("Cafe").build();

        assertTrue(product.getUnitOfMeasures().isEmpty());
    }

    @Test
    void buildCopiesUnitOfMeasuresDefensively() {
        List<String> units = new ArrayList<>(List.of("uom-1", "uom-2"));
        Product product = Product.builder()
                .name("Cafe")
                .unitOfMeasures(units)
                .build();

        units.add("uom-3");

        assertEquals(List.of("uom-1", "uom-2"), product.getUnitOfMeasures());
        assertThrows(
                UnsupportedOperationException.class,
                () -> product.getUnitOfMeasures().add("uom-4")
        );
    }

    @Test
    void stockTransitionsPreserveUnitOfMeasures() {
        Product product = Product.builder()
                .name("Cafe")
                .unitOfMeasures(List.of("uom-1"))
                .stock(4)
                .realCost(10.0)
                .unitRealCost(10.0)
                .unitPublicCost(15.0)
                .build();

        assertEquals(List.of("uom-1"), product.replenish(2).getUnitOfMeasures());
        assertEquals(List.of("uom-1"), product.decreaseStock(1).getUnitOfMeasures());
        assertEquals(List.of("uom-1"), product.withUnitPublicCost(20.0).getUnitOfMeasures());
        assertEquals(List.of("uom-1"), product.withAverageUnitRealCost(12.0).getUnitOfMeasures());
    }
}
