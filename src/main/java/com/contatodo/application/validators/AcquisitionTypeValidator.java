package com.contatodo.application.validators;

import com.contatodo.application.dto.request.CreateAcquisitionTypeRequest;
import com.contatodo.domain.repositories.AcquisitionTypeRepository;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Validator for acquisition type requests.
 */
@Component
public class AcquisitionTypeValidator {

    private final AcquisitionTypeRepository acquisitionTypeRepository;

    /**
     * Creates an acquisition type validator.
     *
     * @param acquisitionTypeRepository Acquisition type repository.
     */
    public AcquisitionTypeValidator(AcquisitionTypeRepository acquisitionTypeRepository) {
        this.acquisitionTypeRepository = acquisitionTypeRepository;
    }

    /**
     * Validates a create acquisition type request.
     *
     * @param request Create acquisition type request.
     * @throws InvalidRequestException if validation fails.
     */
    public void validateCreateRequest(CreateAcquisitionTypeRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new InvalidRequestException(ResponseConstants.VALIDATION_ERROR_MESSAGE, Collections.singletonList("Name is required"));
        }

        acquisitionTypeRepository.findByName(request.getName()).ifPresent(existing -> {
            if (existing.getIsActive() && !existing.getIsDeleted()) {
                throw new InvalidRequestException(ResponseConstants.VALIDATION_ERROR_MESSAGE, 
                    Collections.singletonList("An active acquisition type with this name already exists"));
            }
        });
    }
}
