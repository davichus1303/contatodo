package com.contatodo.application.validators;

import com.contatodo.application.dto.request.CreateExpenseRequest;
import com.contatodo.shared.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ExpenseValidator} unit of measure requirements.
 */
class ExpenseValidatorTest {

    private final ExpenseValidator validator = new ExpenseValidator();

    private CreateExpenseRequest request(String unitOfMeasure) {
        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setName("Papeleria");
        request.setAmount(100.0);
        request.setCurrency("MXN");
        request.setUnitOfMeasure(unitOfMeasure);
        return request;
    }

    @Test
    void createRequestAcceptsAnExpenseWithAUnitOfMeasure() {
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
}
