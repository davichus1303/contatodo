package com.contatodo.application.validators;

import com.contatodo.application.dto.request.CreateAcquisitionRequest;
import com.contatodo.shared.validators.FieldValidator;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Validator for acquisition requests.
 */
@Component
public class AcquisitionValidator {

    private final FieldValidator fieldValidator;

    /**
     * Creates an acquisition validator.
     *
     * @param fieldValidator Shared field validator.
     */
    public AcquisitionValidator(FieldValidator fieldValidator) {
        this.fieldValidator = fieldValidator;
    }

    /**
     * Validates a create acquisition request.
     *
     * @param request Create acquisition request.
     * @throws com.contatodo.shared.exceptions.InvalidRequestException if validation fails.
     */
    public void validateCreateRequest(CreateAcquisitionRequest request) {
        List<String> errors = fieldValidator.createErrorList();

        if (request.getProductName() == null || request.getProductName().trim().isEmpty()) {
            errors.add("Product name is required");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            errors.add("Quantity must be greater than zero");
        }
        if (request.getRealCost() == null || request.getRealCost() <= 0) {
            errors.add("Real cost must be greater than zero");
        }
        if (request.getAcquisitionTypeOid() == null || request.getAcquisitionTypeOid().trim().isEmpty()) {
            errors.add("Acquisition type is required");
        }

        fieldValidator.throwIfHasErrors(errors);
    }

    /**
     * Validates a create acquisition request for non-inventory-affecting acquisitions.
     * Quantity validation is relaxed for these types.
     *
     * @param request Create acquisition request.
     * @throws com.contatodo.shared.exceptions.InvalidRequestException if validation fails.
     */
    public void validateNonInventoryAffectingRequest(CreateAcquisitionRequest request) {
        List<String> errors = fieldValidator.createErrorList();

        if (request.getProductName() == null || request.getProductName().trim().isEmpty()) {
            errors.add("Product name is required");
        }
        // Quantity is optional for non-inventory-affecting acquisitions
        if (request.getRealCost() == null || request.getRealCost() <= 0) {
            errors.add("Real cost must be greater than zero");
        }
        if (request.getAcquisitionTypeOid() == null || request.getAcquisitionTypeOid().trim().isEmpty()) {
            errors.add("Acquisition type is required");
        }

        fieldValidator.throwIfHasErrors(errors);
    }
}
