package com.contatodo.application.validators;

import com.contatodo.application.dto.request.CreateAcquisitionRequest;
import com.contatodo.shared.exceptions.InvalidRequestException;
import com.contatodo.shared.validators.FieldValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AcquisitionValidator} unit of measure requirements.
 */
class AcquisitionValidatorTest {

    private final AcquisitionValidator validator = new AcquisitionValidator(new FieldValidator());

    private CreateAcquisitionRequest request(String unitOfMeasure) {
        CreateAcquisitionRequest request = new CreateAcquisitionRequest();
        request.setAcquisitionTypeOid("type-1");
        request.setProductName("Cafe");
        request.setQuantity(10);
        request.setRealCost(100.0);
        request.setUnitOfMeasure(unitOfMeasure);
        return request;
    }

    @Test
    void createRequestAcceptsAnAcquisitionWithAUnitOfMeasure() {
        assertDoesNotThrow(() -> validator.validateCreateRequest(request("uom-1")));
    }

    @Test
    void createRequestRejectsAMissingUnitOfMeasure() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validateCreateRequest(request(null))
        );

        assertTrue(exception.getDetails().contains("Unit of measure is required"));
    }

    @Test
    void createRequestRejectsABlankUnitOfMeasure() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validateCreateRequest(request("  "))
        );

        assertTrue(exception.getDetails().contains("Unit of measure is required"));
    }

    @Test
    void createRequestAcceptsAPositivePackSize() {
        CreateAcquisitionRequest request = request("uom-1");
        request.setPackSize(12);

        assertDoesNotThrow(() -> validator.validateCreateRequest(request));
    }

    @Test
    void createRequestRejectsANonPositivePackSize() {
        CreateAcquisitionRequest request = request("uom-1");
        request.setPackSize(0);

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validateCreateRequest(request)
        );

        assertTrue(exception.getDetails().stream()
                .anyMatch(detail -> detail.contains("Pack size must be greater than zero")));
    }

    @Test
    void nonInventoryAffectingRequestAcceptsAnAcquisitionWithAUnitOfMeasure() {
        assertDoesNotThrow(() -> validator.validateNonInventoryAffectingRequest(request("uom-1")));
    }

    @Test
    void nonInventoryAffectingRequestRejectsAMissingUnitOfMeasure() {
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validateNonInventoryAffectingRequest(request(null))
        );

        assertTrue(exception.getDetails().contains("Unit of measure is required"));
    }
}
