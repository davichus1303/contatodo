package com.contatodo.application.validators;

import com.contatodo.application.dto.request.CreateProductRequest;
import com.contatodo.application.dto.request.UpdateProductRequest;
import com.contatodo.shared.constants.ProductConstants;
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
        fieldValidator.validateOptionalPositive(request.getPackSize(), ProductConstants.PRODUCT_PACK_SIZE_INVALID, errors);
        validateUnitOfMeasuresRequired(request.getUnitOfMeasures(), errors);
        fieldValidator.throwIfHasErrors(errors);
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
        fieldValidator.validateOptionalPositive(request.getPackSize(), ProductConstants.PRODUCT_PACK_SIZE_INVALID, errors);
        validateUnitOfMeasuresIfPresent(request.getUnitOfMeasures(), errors);
        fieldValidator.throwIfHasErrors(errors);
    }

    /**
     * Validates that a new product references at least one usable unit of measure.
     *
     * @param unitOfMeasures Unit of measure identifiers supplied in the request.
     * @throws com.contatodo.shared.exceptions.InvalidRequestException if the list is missing or invalid.
     */
    public void validateRequiredUnitOfMeasures(List<String> unitOfMeasures) {
        List<String> errors = fieldValidator.createErrorList();
        validateUnitOfMeasuresRequired(unitOfMeasures, errors);
        fieldValidator.throwIfHasErrors(errors);
    }

    /**
     * Validates that the product references at least one usable unit of measure.
     *
     * @param unitOfMeasures Unit of measure identifiers supplied in the request.
     * @param errors Error list to append to.
     */
    private void validateUnitOfMeasuresRequired(List<String> unitOfMeasures, List<String> errors) {
        if (isMissingOrEmpty(unitOfMeasures) || hasInvalidEntries(unitOfMeasures)) {
            errors.add(ProductConstants.PRODUCT_UNIT_OF_MEASURE_REQUIRED);
        }
    }

    /**
     * Validates supplied unit of measure entries without requiring the list.
     *
     * @param unitOfMeasures Unit of measure identifiers supplied in the request.
     * @param errors Error list to append to.
     */
    private void validateUnitOfMeasuresIfPresent(List<String> unitOfMeasures, List<String> errors) {
        if (!isMissingOrEmpty(unitOfMeasures) && hasInvalidEntries(unitOfMeasures)) {
            errors.add(ProductConstants.PRODUCT_UNIT_OF_MEASURE_REQUIRED);
        }
    }

    /**
     * Checks whether the list is absent or empty.
     *
     * @param unitOfMeasures Unit of measure identifiers.
     * @return True when the list has no entries.
     */
    private boolean isMissingOrEmpty(List<String> unitOfMeasures) {
        return unitOfMeasures == null || unitOfMeasures.isEmpty();
    }

    /**
     * Checks whether any supplied identifier is null or blank.
     *
     * @param unitOfMeasures Unit of measure identifiers.
     * @return True when at least one entry is invalid.
     */
    private boolean hasInvalidEntries(List<String> unitOfMeasures) {
        return unitOfMeasures.stream().anyMatch(value -> value == null || value.isBlank());
    }
}
