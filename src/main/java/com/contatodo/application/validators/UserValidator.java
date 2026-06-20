package com.contatodo.application.validators;

import com.contatodo.application.dto.request.CreateUserRequest;
import com.contatodo.application.dto.request.LoginRequest;
import com.contatodo.application.dto.request.UpdateUserRequest;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.constants.UserConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import com.contatodo.shared.validators.FieldValidator;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Validator for user request DTOs.
 */
@Component
public class UserValidator {

    private final FieldValidator fieldValidator;

    /**
     * Creates a user validator.
     *
     * @param fieldValidator Shared field validator.
     */
    public UserValidator(FieldValidator fieldValidator) {
        this.fieldValidator = fieldValidator;
    }

    /**
     * Validates a create user request.
     *
     * @param request Create user request.
     */
    public void validateCreateRequest(CreateUserRequest request) {
        List<String> errors = fieldValidator.createErrorList();
        fieldValidator.validateRequired(request.getUserName(), UserConstants.USER_USERNAME_REQUIRED, errors);
        fieldValidator.validateRequired(request.getEmail(), UserConstants.USER_EMAIL_REQUIRED, errors);
        fieldValidator.validateRequired(request.getPassword(), UserConstants.USER_PASSWORD_REQUIRED, errors);
        fieldValidator.validateRequired(request.getName(), UserConstants.USER_NAME_REQUIRED, errors);
        fieldValidator.validateEmail(request.getEmail(), UserConstants.USER_INVALID_EMAIL, errors);
        throwIfHasErrors(errors);
    }

    /**
     * Validates an update user request.
     *
     * @param request Update user request.
     */
    public void validateUpdateRequest(UpdateUserRequest request) {
        List<String> errors = fieldValidator.createErrorList();
        if (request.getEmail() != null) {
            fieldValidator.validateEmail(request.getEmail(), UserConstants.USER_INVALID_EMAIL, errors);
        }
        throwIfHasErrors(errors);
    }

    /**
     * Validates a login request.
     *
     * @param request Login request.
     */
    public void validateLoginRequest(LoginRequest request) {
        List<String> errors = fieldValidator.createErrorList();
        fieldValidator.validateRequired(request.getEmail(), UserConstants.USER_EMAIL_REQUIRED, errors);
        fieldValidator.validateRequired(request.getPassword(), UserConstants.USER_PASSWORD_REQUIRED, errors);
        fieldValidator.validateEmail(request.getEmail(), UserConstants.USER_INVALID_EMAIL, errors);
        throwIfHasErrors(errors);
    }

    private void throwIfHasErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new InvalidRequestException(ResponseConstants.VALIDATION_ERROR_MESSAGE, errors);
        }
    }
}
