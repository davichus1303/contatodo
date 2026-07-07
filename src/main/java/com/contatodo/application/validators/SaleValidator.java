package com.contatodo.application.validators;

import com.contatodo.application.dto.request.CreateSaleRequest;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.constants.SaleConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import com.contatodo.shared.validators.FieldValidator;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Validator for sale request DTOs.
 */
@Component
public class SaleValidator {

    private final FieldValidator fieldValidator;

    /**
     * Creates a sale validator.
     *
     * @param fieldValidator Shared field validator.
     */
    public SaleValidator(FieldValidator fieldValidator) {
        this.fieldValidator = fieldValidator;
    }

    /**
     * Validates a create sale request.
     *
     * @param request Create sale request.
     */
    public void validateCreateRequest(CreateSaleRequest request) {
        List<String> errors = fieldValidator.createErrorList();
        fieldValidator.validateRequired(request.getProductOid(), SaleConstants.SALE_PRODUCT_OID_REQUIRED, errors);
        fieldValidator.validateNonNegative(request.getQuantity(), SaleConstants.SALE_QUANTITY_REQUIRED, errors);
        fieldValidator.validateNonNegative(request.getTotalSalePrice(), SaleConstants.SALE_TOTAL_SALE_PRICE_REQUIRED, errors);
        throwIfHasErrors(errors);
    }

    private void throwIfHasErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new InvalidRequestException(ResponseConstants.VALIDATION_ERROR_MESSAGE, errors);
        }
    }
}
