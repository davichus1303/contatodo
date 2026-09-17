package com.contatodo.application.validators;

import com.contatodo.application.dto.request.CreateCompaniesRequest;
import com.contatodo.application.dto.request.CreateCompanyRequest;
import com.contatodo.application.dto.request.UpdateCompanyRequest;
import com.contatodo.shared.constants.CompanyConstants;
import com.contatodo.shared.validators.FieldValidator;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Validator for company request DTOs.
 */
@Component
public class CompanyValidator {

    private final FieldValidator fieldValidator;

    /**
     * Creates a company validator.
     *
     * @param fieldValidator Shared field validator.
     */
    public CompanyValidator(FieldValidator fieldValidator) {
        this.fieldValidator = fieldValidator;
    }

    /**
     * Validates a bulk create companies request.
     *
     * <p>The whole batch is validated before anything is persisted, so an
     * invalid entry rejects the complete request.</p>
     *
     * @param request Create companies request.
     */
    public void validateCreateRequest(CreateCompaniesRequest request) {
        List<String> errors = fieldValidator.createErrorList();

        if (request == null || request.getCompanies() == null || request.getCompanies().isEmpty()) {
            errors.add(CompanyConstants.COMPANIES_REQUIRED);
            fieldValidator.throwIfHasErrors(errors);
            return;
        }

        for (CreateCompanyRequest company : request.getCompanies()) {
            if (company == null) {
                errors.add(CompanyConstants.COMPANY_REQUIRED);
                continue;
            }
            fieldValidator.validateRequired(company.getName(), CompanyConstants.COMPANY_NAME_REQUIRED, errors);
        }

        fieldValidator.throwIfHasErrors(errors);
    }

    /**
     * Validates an update company request.
     *
     * <p>Every field is optional; a provided name must not be blank.</p>
     *
     * @param request Update company request.
     */
    public void validateUpdateRequest(UpdateCompanyRequest request) {
        List<String> errors = fieldValidator.createErrorList();

        if (request == null) {
            errors.add(CompanyConstants.COMPANY_REQUIRED);
            fieldValidator.throwIfHasErrors(errors);
            return;
        }

        if (request.getName() != null) {
            fieldValidator.validateRequired(request.getName(), CompanyConstants.COMPANY_NAME_REQUIRED, errors);
        }

        fieldValidator.throwIfHasErrors(errors);
    }
}
