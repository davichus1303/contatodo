package com.contatodo.application.validators;

import com.contatodo.application.dto.request.CreateProductRequest;
import com.contatodo.application.dto.request.UpdateProductRequest;
import com.contatodo.shared.constants.ProductConstants;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import com.contatodo.shared.validators.FieldValidator;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Validator for product request DTOs.
 */
@Component
public class ProductValidator {

    private final FieldValidator fieldValidator;

    /**
     * Creates a product validator.
     *
     * @param fieldValidator Shared field validator.
     */
    public ProductValidator(FieldValidator fieldValidator) {
        this.fieldValidator = fieldValidator;
    }

    /**
     * Validates a create product request.
     *
     * @param request Create product request.
     */
    public void validateCreateRequest(CreateProductRequest request) {
        List<String> errors = fieldValidator.createErrorList();
        fieldValidator.validateRequired(request.getName(), ProductConstants.PRODUCT_NAME_REQUIRED, errors);
        fieldValidator.validateRequired(request.getDescription(), ProductConstants.PRODUCT_DESCRIPTION_REQUIRED, errors);
        fieldValidator.validateNonNegative(request.getStock(), ProductConstants.PRODUCT_STOCK_REQUIRED, errors);
        fieldValidator.validateNonNegative(request.getRealCost(), ProductConstants.PRODUCT_REAL_COST_REQUIRED, errors);
        fieldValidator.validateNonNegative(request.getUnitRealCost(), ProductConstants.PRODUCT_REAL_COST_REQUIRED, errors);
        fieldValidator.validateNonNegative(request.getUnitPublicCost(), ProductConstants.PRODUCT_PUBLIC_COST_REQUIRED, errors);
        throwIfHasErrors(errors);
    }

    /**
     * Validates an update product request.
     *
     * @param request Update product request.
     */
    public void validateUpdateRequest(UpdateProductRequest request) {
        List<String> errors = fieldValidator.createErrorList();
        if (request.getStock() != null) {
            fieldValidator.validateNonNegative(request.getStock(), ProductConstants.PRODUCT_STOCK_INVALID, errors);
        }
        if (request.getRealCost() != null) {
            fieldValidator.validateNonNegative(request.getRealCost(), ProductConstants.PRODUCT_COST_INVALID, errors);
        }
        if (request.getUnitRealCost() != null) {
            fieldValidator.validateNonNegative(request.getUnitRealCost(), ProductConstants.PRODUCT_COST_INVALID, errors);
        }
        if (request.getUnitPublicCost() != null) {
            fieldValidator.validateNonNegative(request.getUnitPublicCost(), ProductConstants.PRODUCT_COST_INVALID, errors);
        }
        throwIfHasErrors(errors);
    }

    private void throwIfHasErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new InvalidRequestException(ResponseConstants.VALIDATION_ERROR_MESSAGE, errors);
        }
    }
}
