package com.contatodo.application.validators;

import com.contatodo.application.dto.request.CreateProductRequest;
import com.contatodo.application.dto.request.UpdateProductRequest;
import com.contatodo.shared.constants.ProductConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import com.contatodo.shared.validators.FieldValidator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ProductValidator} unit of measure requirements.
 */
class ProductValidatorTest {

    private final ProductValidator validator = new ProductValidator(new FieldValidator());

    private CreateProductRequest createRequest(List<String> unitOfMeasures) {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Cafe");
        request.setDescription("Grano de cafe");
        request.setUnitOfMeasures(unitOfMeasures);
        request.setStock(10);
        request.setRealCost(100.0);
        request.setUnitRealCost(10.0);
        request.setUnitPublicCost(15.0);
        return request;
    }

    private UpdateProductRequest updateRequest(List<String> unitOfMeasures) {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setUnitOfMeasures(unitOfMeasures);
        return request;
    }

    @Test
    void createRequestAcceptsAProductWithUnitsOfMeasure() {
        assertDoesNotThrow(() -> validator.validateCreateRequest(createRequest(List.of("uom-1"))));
    }

    @Test
    void createRequestRejectsMissingUnitsOfMeasure() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validateCreateRequest(createRequest(null))
        );

        assertTrue(exception.getDetails().contains(ProductConstants.PRODUCT_UNIT_OF_MEASURE_REQUIRED));
    }

    @Test
    void createRequestRejectsAnEmptyUnitsOfMeasureList() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validateCreateRequest(createRequest(List.of()))
        );

        assertTrue(exception.getDetails().contains(ProductConstants.PRODUCT_UNIT_OF_MEASURE_REQUIRED));
    }

    @Test
    void createRequestRejectsBlankUnitsOfMeasureEntries() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validateCreateRequest(createRequest(List.of("uom-1", "  ")))
        );

        assertTrue(exception.getDetails().contains(ProductConstants.PRODUCT_UNIT_OF_MEASURE_REQUIRED));
    }

    @Test
    void updateRequestAcceptsAProductWithUnitsOfMeasure() {
        assertDoesNotThrow(() -> validator.validateUpdateRequest(updateRequest(List.of("uom-1"))));
    }

    @Test
    void updateRequestRejectsMissingUnitsOfMeasure() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validateUpdateRequest(updateRequest(null))
        );

        assertTrue(exception.getDetails().contains(ProductConstants.PRODUCT_UNIT_OF_MEASURE_REQUIRED));
    }

    @Test
    void updateRequestRejectsAnEmptyUnitsOfMeasureList() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validateUpdateRequest(updateRequest(List.of()))
        );

        assertTrue(exception.getDetails().contains(ProductConstants.PRODUCT_UNIT_OF_MEASURE_REQUIRED));
    }
}
