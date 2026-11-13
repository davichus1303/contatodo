package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateProductRequest;
import com.contatodo.application.dto.request.UpdateProductRequest;
import com.contatodo.application.dto.response.ProductResponse;
import com.contatodo.application.dto.response.UnitOfMeasureDetailResponse;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.UnitOfMeasure;
import com.contatodo.domain.model.CompanyOid;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link ProductMapper} unit of measure mapping.
 */
class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapper();

    private CreateProductRequest createRequest(List<String> unitOfMeasures) {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Cafe");
        request.setDescription("Grano de cafe");
        request.setUnitOfMeasures(unitOfMeasures);
        request.setPackSize(12);
        request.setStock(10);
        request.setRealCost(100.0);
        request.setUnitRealCost(10.0);
        request.setUnitPublicCost(15.0);
        request.setIsActive(true);
        return request;
    }

    private Product existingProduct() {
        return Product.builder()
                .id("p-1")
                .name("Cafe")
                .description("Grano de cafe")
                .unitOfMeasures(List.of("uom-1"))
                .packSize(12)
                .stock(10)
                .code("42")
                .realCost(100.0)
                .unitRealCost(10.0)
                .unitPublicCost(15.0)
                .isActive(true)
                .byUserOid("user-1")
                .build();
    }

    @Test
    void toEntityMapsUnitsOfMeasureFromTheCreateRequest() {
        Product product = mapper.toEntity(
                createRequest(List.of("uom-1", "uom-2")),
                "42",
                "user-1",
                CompanyOid.of("company-1")
        );

        assertEquals(List.of("uom-1", "uom-2"), product.getUnitOfMeasures());
    }

    @Test
    void toEntityMapsPackSizeFromTheCreateRequest() {
        Product product = mapper.toEntity(
                createRequest(List.of("uom-1")),
                "42",
                "user-1",
                CompanyOid.of("company-1")
        );

        assertEquals(12, product.getPackSize());
    }

    @Test
    void applyUpdateReplacesThePackSizeWhenProvided() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setPackSize(24);

        Product updated = mapper.applyUpdate(existingProduct(), request, "editor-1");

        assertEquals(24, updated.getPackSize());
    }

    @Test
    void applyUpdateKeepsTheExistingPackSizeWhenOmitted() {
        Product updated = mapper.applyUpdate(existingProduct(), new UpdateProductRequest(), "editor-1");

        assertEquals(12, updated.getPackSize());
    }

    @Test
    void applyUpdateReplacesUnitsOfMeasureWhenProvided() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setUnitOfMeasures(List.of("uom-2", "uom-3"));

        Product updated = mapper.applyUpdate(existingProduct(), request, "editor-1");

        assertEquals(List.of("uom-2", "uom-3"), updated.getUnitOfMeasures());
    }

    @Test
    void applyUpdateKeepsExistingUnitsOfMeasureWhenOmitted() {
        Product updated = mapper.applyUpdate(existingProduct(), new UpdateProductRequest(), "editor-1");

        assertEquals(List.of("uom-1"), updated.getUnitOfMeasures());
    }

    @Test
    void applyUpdateKeepsExistingUnitsOfMeasureWhenTheListIsEmpty() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setUnitOfMeasures(List.of());

        Product updated = mapper.applyUpdate(existingProduct(), request, "editor-1");

        assertEquals(List.of("uom-1"), updated.getUnitOfMeasures());
    }

    @Test
    void toResponseExposesUnitsOfMeasure() {
        ProductResponse response = mapper.toResponse(existingProduct());

        assertEquals(List.of("uom-1"), response.getUnitOfMeasures());
    }

    @Test
    void toResponseExposesPackSize() {
        ProductResponse response = mapper.toResponse(existingProduct());

        assertEquals(12, response.getPackSize());
    }

    @Test
    void toResponseListWithUnitOfMeasureDetailsJoinsNameAndOid() {
        Product product = Product.builder()
                .id("p-1")
                .name("Cafe")
                .unitOfMeasures(List.of("uom-1", "uom-missing", "uom-2"))
                .stock(10)
                .build();
        UnitOfMeasure kilogram = unitOfMeasure("uom-1", "Kilogramo");
        UnitOfMeasure box = unitOfMeasure("uom-2", "Caja");

        List<ProductResponse> responses = mapper.toResponseListWithUnitOfMeasureDetails(
                List.of(product),
                Map.of("uom-1", kilogram, "uom-2", box)
        );

        List<UnitOfMeasureDetailResponse> details = responses.get(0).getUnitOfMeasureDetails();
        assertEquals(2, details.size());
        assertEquals("uom-1", details.get(0).getOid());
        assertEquals("Kilogramo", details.get(0).getName());
        assertEquals("uom-2", details.get(1).getOid());
        assertEquals("Caja", details.get(1).getName());
    }

    private UnitOfMeasure unitOfMeasure(String id, String name) {
        return UnitOfMeasure.builder()
                .id(id)
                .name(name)
                .abrev(name.substring(0, 1))
                .companyOid("company-1")
                .isActive(true)
                .isDeleted(false)
                .build();
    }
}
