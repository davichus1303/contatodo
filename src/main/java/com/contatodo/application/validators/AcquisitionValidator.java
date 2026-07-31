package com.contatodo.application.validators;

import com.contatodo.application.dto.request.CreateAcquisitionRequest;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for acquisition requests.
 */
@Component
public class AcquisitionValidator {

    /**
     * Validates a create acquisition request.
     *
     * @param request Create acquisition request.
     * @throws InvalidRequestException if validation fails.
     */
    public void validateCreateRequest(CreateAcquisitionRequest request) {
        List<String> errors = new ArrayList<>();
        
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
        
        if (!errors.isEmpty()) {
            throw new InvalidRequestException(ResponseConstants.VALIDATION_ERROR_MESSAGE, errors);
        }
    }
}
