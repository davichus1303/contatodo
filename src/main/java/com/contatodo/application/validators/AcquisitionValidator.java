package com.contatodo.application.validators;

import com.contatodo.application.dto.request.CreateAcquisitionRequest;
import com.contatodo.shared.constants.AcquisitionConstants;
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
            errors.add(AcquisitionConstants.PRODUCT_NAME_REQUIRED);
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            errors.add(AcquisitionConstants.QUANTITY_GREATER_THAN_ZERO);
        }
        if (request.getRealCost() == null || request.getRealCost() <= 0) {
            errors.add(AcquisitionConstants.REAL_COST_GREATER_THAN_ZERO);
        }
        if (request.getAcquisitionTypeOid() == null || request.getAcquisitionTypeOid().trim().isEmpty()) {
            errors.add(AcquisitionConstants.ACQUISITION_TYPE_REQUIRED);
        }
        if (request.getUnitOfMeasure() == null || request.getUnitOfMeasure().trim().isEmpty()) {
            errors.add(AcquisitionConstants.UNIT_OF_MEASURE_REQUIRED);
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
            errors.add(AcquisitionConstants.PRODUCT_NAME_REQUIRED);
        }
        // Quantity is optional for non-inventory-affecting acquisitions
        if (request.getRealCost() == null || request.getRealCost() <= 0) {
            errors.add(AcquisitionConstants.REAL_COST_GREATER_THAN_ZERO);
        }
        if (request.getAcquisitionTypeOid() == null || request.getAcquisitionTypeOid().trim().isEmpty()) {
            errors.add(AcquisitionConstants.ACQUISITION_TYPE_REQUIRED);
        }
        if (request.getUnitOfMeasure() == null || request.getUnitOfMeasure().trim().isEmpty()) {
            errors.add(AcquisitionConstants.UNIT_OF_MEASURE_REQUIRED);
        }

        fieldValidator.throwIfHasErrors(errors);
    }
}
