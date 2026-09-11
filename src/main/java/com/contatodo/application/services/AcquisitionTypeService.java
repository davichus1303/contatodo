package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateAcquisitionTypeRequest;
import com.contatodo.application.dto.request.UpdateAcquisitionTypeRequest;
import com.contatodo.application.dto.response.AcquisitionTypeResponse;
import com.contatodo.application.mapper.AcquisitionTypeMapper;
import com.contatodo.application.validators.AcquisitionTypeValidator;
import com.contatodo.domain.entities.AcquisitionType;
import com.contatodo.domain.repositories.AcquisitionTypeRepository;
import com.contatodo.application.port.AuthenticatedUserProvider;
import org.springframework.stereotype.Service;
import com.contatodo.shared.constants.AcquisitionTypeConstants;
import com.contatodo.shared.exceptions.AcquisitionTypeNotFoundException;

import java.util.List;

/**
 * Service containing acquisition type business logic.
 */
@Service
public class AcquisitionTypeService {

    private final AcquisitionTypeRepository acquisitionTypeRepository;
    private final AcquisitionTypeValidator acquisitionTypeValidator;
    private final AcquisitionTypeMapper acquisitionTypeMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    /**
     * Creates an acquisition type service.
     *
     * @param acquisitionTypeRepository Acquisition type repository port.
     * @param acquisitionTypeValidator Acquisition type validator.
     * @param acquisitionTypeMapper Acquisition type mapper.
     * @param authenticatedUserProvider Authenticated user provider.
     */
    public AcquisitionTypeService(
            AcquisitionTypeRepository acquisitionTypeRepository,
            AcquisitionTypeValidator acquisitionTypeValidator,
            AcquisitionTypeMapper acquisitionTypeMapper,
            AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.acquisitionTypeRepository = acquisitionTypeRepository;
        this.acquisitionTypeValidator = acquisitionTypeValidator;
        this.acquisitionTypeMapper = acquisitionTypeMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    /**
     * Creates a new acquisition type.
     *
     * @param request Create acquisition type request.
     * @return Created acquisition type response.
     */
    public AcquisitionTypeResponse createAcquisitionType(CreateAcquisitionTypeRequest request) {
        acquisitionTypeValidator.validateCreateRequest(request);

        String userOid = authenticatedUserProvider.getCurrentUserOid();
        AcquisitionType acquisitionType = acquisitionTypeMapper.toEntity(request, userOid);
        AcquisitionType savedAcquisitionType = acquisitionTypeRepository.save(acquisitionType);
        return acquisitionTypeMapper.toResponse(savedAcquisitionType);
    }

    /**
     * Retrieves all active and non-deleted acquisition types.
     *
     * @return List of acquisition type responses.
     */
    public List<AcquisitionTypeResponse> getActiveAcquisitionTypes() {
        List<AcquisitionType> acquisitionTypes = acquisitionTypeRepository.findActiveAndNotDeleted();
        return acquisitionTypeMapper.toResponseList(acquisitionTypes);
    }

    /**
     * Updates an existing acquisition type.
     *
     * @param id Acquisition type ID.
     * @param request Update acquisition type request.
     * @return Updated acquisition type response.
     */
    public AcquisitionTypeResponse updateAcquisitionType(String id, UpdateAcquisitionTypeRequest request) {
        AcquisitionType existingAcquisitionType = acquisitionTypeRepository.findById(id)
            .orElse(null);
        
        acquisitionTypeValidator.validateUpdateRequest(request, existingAcquisitionType);

        AcquisitionType updatedAcquisitionType =
                acquisitionTypeRepository.save(acquisitionTypeMapper.updateEntityFromRequest(existingAcquisitionType, request));
        return acquisitionTypeMapper.toResponse(updatedAcquisitionType);
    }

    /**
     * Logically deletes an acquisition type.
     *
     * @param id Acquisition type ID.
     * @return Deleted acquisition type response.
     */
    public AcquisitionTypeResponse deleteAcquisitionType(String id) {
        AcquisitionType existingAcquisitionType = acquisitionTypeRepository.findById(id)
            .orElse(null);

        if (existingAcquisitionType == null) {
            throw new AcquisitionTypeNotFoundException(AcquisitionTypeConstants.NOT_FOUND_ERROR);
        }

        if (Boolean.TRUE.equals(existingAcquisitionType.getIsDeleted())) {
            throw new AcquisitionTypeNotFoundException(AcquisitionTypeConstants.ALREADY_DELETED_ERROR);
        }

        AcquisitionType deletedAcquisitionType = acquisitionTypeRepository.save(existingAcquisitionType.markDeleted());
        return acquisitionTypeMapper.toResponse(deletedAcquisitionType);
    }

    /**
     * Retrieves all non-deleted acquisition types (both active and inactive) for administration.
     *
     * @return List of acquisition type responses ordered by name.
     */
    public List<AcquisitionTypeResponse> getAllNotDeletedAcquisitionTypes() {
        List<AcquisitionType> acquisitionTypes = acquisitionTypeRepository.findAllNotDeleted();
        return acquisitionTypeMapper.toResponseList(acquisitionTypes);
    }
}
