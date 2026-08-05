package com.contatodo.application.validators;

import com.contatodo.application.dto.request.CreateAcquisitionTypeRequest;
import com.contatodo.application.dto.request.UpdateAcquisitionTypeRequest;
import com.contatodo.domain.entities.AcquisitionType;
import com.contatodo.domain.repositories.AcquisitionTypeRepository;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    /**
     * Validates an update acquisition type request.
     *
     * @param request Update acquisition type request.
     * @param existingAcquisitionType Existing acquisition type entity.
     * @throws InvalidRequestException if validation fails.
     */
    public void validateUpdateRequest(UpdateAcquisitionTypeRequest request, AcquisitionType existingAcquisitionType) {
        List<String> errors = new ArrayList<>();

        if (existingAcquisitionType == null) {
            errors.add("Acquisition type not found");
        } else {
            if (existingAcquisitionType.getIsDeleted()) {
                errors.add("Cannot update a deleted acquisition type");
            }
        }

        if (request.getName() != null) {
            if (request.getName().trim().isEmpty()) {
                errors.add("Name cannot be empty");
            } else {
                acquisitionTypeRepository.findByName(request.getName()).ifPresent(existing -> {
                    if (!existing.getId().equals(existingAcquisitionType.getId()) 
                        && existing.getIsActive() 
                        && !existing.getIsDeleted()) {
                        errors.add("An active acquisition type with this name already exists");
                    }
                });
            }
        }

        if (!errors.isEmpty()) {
            throw new InvalidRequestException(ResponseConstants.VALIDATION_ERROR_MESSAGE, errors);
        }
    }
}
