package com.contatodo.application.validators;

import com.contatodo.application.dto.request.CreateExpenseRequest;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Validator for expense requests.
 */
@Component
public class ExpenseValidator {

    /**
     * Validates a create expense request.
     *
     * @param request Create expense request.
     * @throws InvalidRequestException if validation fails.
     */
    public void validateCreateRequest(CreateExpenseRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new InvalidRequestException(ResponseConstants.VALIDATION_ERROR_MESSAGE, Collections.singletonList("Name is required"));
        }

        // Quantity is optional for expenses (can be 1 for non-inventory acquisitions)
        if (request.getQuantity() != null && request.getQuantity() <= 0) {
            throw new InvalidRequestException(ResponseConstants.VALIDATION_ERROR_MESSAGE, Collections.singletonList("Quantity must be greater than 0"));
        }

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new InvalidRequestException(ResponseConstants.VALIDATION_ERROR_MESSAGE, Collections.singletonList("Amount must be greater than 0"));
        }

        if (request.getCurrency() == null || request.getCurrency().trim().isEmpty()) {
            throw new InvalidRequestException(ResponseConstants.VALIDATION_ERROR_MESSAGE, Collections.singletonList("Currency is required"));
        }
    }
}
