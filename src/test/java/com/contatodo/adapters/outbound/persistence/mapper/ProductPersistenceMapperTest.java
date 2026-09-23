package com.contatodo.adapters.outbound.persistence.mapper;

import com.contatodo.adapters.outbound.persistence.document.ProductDocument;
import com.contatodo.domain.entities.Product;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link ProductPersistenceMapper}.
 */
class ProductPersistenceMapperTest {

    private final ProductPersistenceMapper mapper = new ProductPersistenceMapper();

    @Test
    void toDocumentMapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Product product = Product.builder()
                .id("prod-1")
                .name("Cafe")
                .description("Grano de cafe")
                .stock(10)
                .code("42")
                .realCost(50.0)
                .unitRealCost(10.0)
                .unitPublicCost(15.0)
                .urlPhoto("http://example.com/photo.jpg")
                .isActive(true)
                .userOid("user-1")
                .createdDate(now)
                .updatedDate(now)
                .build();

        ProductDocument document = mapper.toDocument(product);

        assertEquals("prod-1", document.getId());
        assertEquals("Cafe", document.getName());
        assertEquals("Grano de cafe", document.getDescription());
        assertEquals(10, document.getStock());
        assertEquals("42", document.getCode());
        assertEquals(50.0, document.getRealCost());
        assertEquals(10.0, document.getUnitRealCost());
        assertEquals(15.0, document.getUnitPublicCost());
        assertEquals("http://example.com/photo.jpg", document.getUrlPhoto());
        assertEquals(true, document.getIsActive());
        assertEquals("user-1", document.getUserOid());
        assertEquals(now, document.getCreatedDate());
        assertEquals(now, document.getUpdatedDate());
    }

    @Test
    void toEntityMapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        ProductDocument document = new ProductDocument();
        document.setId("prod-1");
        document.setName("Cafe");
        document.setDescription("Grano de cafe");
        document.setStock(10);
        document.setCode("42");
        document.setRealCost(50.0);
        document.setUnitRealCost(10.0);
        document.setUnitPublicCost(15.0);
        document.setUrlPhoto("http://example.com/photo.jpg");
        document.setIsActive(true);
        document.setUserOid("user-1");
        document.setCreatedDate(now);
        document.setUpdatedDate(now);

        Product product = mapper.toEntity(document);

        assertEquals("prod-1", product.getId());
        assertEquals("Cafe", product.getName());
        assertEquals("Grano de cafe", product.getDescription());
        assertEquals(10, product.getStock());
        assertEquals("42", product.getCode());
        assertEquals(50.0, product.getRealCost());
        assertEquals(10.0, product.getUnitRealCost());
        assertEquals(15.0, product.getUnitPublicCost());
        assertEquals("http://example.com/photo.jpg", product.getUrlPhoto());
        assertEquals(true, product.getIsActive());
        assertEquals("user-1", product.getUserOid());
        assertEquals(now, product.getCreatedDate());
        assertEquals(now, product.getUpdatedDate());
    }

    @Test
    void toEntityHandlesNullUrlPhoto() {
        LocalDateTime now = LocalDateTime.now();
        ProductDocument document = new ProductDocument();
        document.setId("prod-1");
        document.setName("Cafe");
        document.setDescription("Grano de cafe");
        document.setStock(10);
        document.setCode("42");
        document.setRealCost(50.0);
        document.setUnitRealCost(10.0);
        document.setUnitPublicCost(15.0);
        document.setUrlPhoto(null);
        document.setIsActive(true);
        document.setUserOid("user-1");
        document.setCreatedDate(now);
        document.setUpdatedDate(now);

        Product product = mapper.toEntity(document);

        assertNull(product.getUrlPhoto());
    }

    @Test
    void roundTripPreservesData() {
        LocalDateTime now = LocalDateTime.now();
        Product original = Product.builder()
                .id("prod-1")
                .name("Cafe")
                .description("Grano de cafe")
                .stock(10)
                .code("42")
                .realCost(50.0)
                .unitRealCost(10.0)
                .unitPublicCost(15.0)
                .urlPhoto("http://example.com/photo.jpg")
                .isActive(true)
                .userOid("user-1")
                .createdDate(now)
                .updatedDate(now)
                .build();

        ProductDocument document = mapper.toDocument(original);
        Product roundTripped = mapper.toEntity(document);

        assertEquals(original.getId(), roundTripped.getId());
        assertEquals(original.getName(), roundTripped.getName());
        assertEquals(original.getDescription(), roundTripped.getDescription());
        assertEquals(original.getStock(), roundTripped.getStock());
        assertEquals(original.getCode(), roundTripped.getCode());
        assertEquals(original.getRealCost(), roundTripped.getRealCost());
        assertEquals(original.getUnitRealCost(), roundTripped.getUnitRealCost());
        assertEquals(original.getUnitPublicCost(), roundTripped.getUnitPublicCost());
        assertEquals(original.getUrlPhoto(), roundTripped.getUrlPhoto());
        assertEquals(original.getIsActive(), roundTripped.getIsActive());
        assertEquals(original.getUserOid(), roundTripped.getUserOid());
    }
}