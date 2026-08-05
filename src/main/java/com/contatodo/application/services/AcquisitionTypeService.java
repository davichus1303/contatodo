package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateAcquisitionTypeRequest;
import com.contatodo.application.dto.request.UpdateAcquisitionTypeRequest;
import com.contatodo.application.dto.response.AcquisitionTypeResponse;
import com.contatodo.application.mapper.AcquisitionTypeMapper;
import com.contatodo.application.validators.AcquisitionTypeValidator;
import com.contatodo.domain.entities.AcquisitionType;
import com.contatodo.domain.repositories.AcquisitionTypeRepository;
import com.contatodo.shared.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import com.contatodo.shared.constants.AqcisitionTypeConstants;

import java.util.List;

/**
 * Service containing acquisition type business logic.
 */
@Service
public class AcquisitionTypeService {

    private final AcquisitionTypeRepository acquisitionTypeRepository;
    private final AcquisitionTypeValidator acquisitionTypeValidator;
    private final AcquisitionTypeMapper acquisitionTypeMapper;
    private final UserService userService;

    /**
     * Creates an acquisition type service.
     *
     * @param acquisitionTypeRepository Acquisition type repository port.
     * @param acquisitionTypeValidator Acquisition type validator.
     * @param acquisitionTypeMapper Acquisition type mapper.
     * @param userService User service for security context.
     */
    public AcquisitionTypeService(
            AcquisitionTypeRepository acquisitionTypeRepository,
            AcquisitionTypeValidator acquisitionTypeValidator,
            AcquisitionTypeMapper acquisitionTypeMapper,
            UserService userService
    ) {
        this.acquisitionTypeRepository = acquisitionTypeRepository;
        this.acquisitionTypeValidator = acquisitionTypeValidator;
        this.acquisitionTypeMapper = acquisitionTypeMapper;
        this.userService = userService;
    }

    /**
     * Creates a new acquisition type.
     *
     * @param request Create acquisition type request.
     * @return Created acquisition type response.
     */
    public AcquisitionTypeResponse createAcquisitionType(CreateAcquisitionTypeRequest request) {
        acquisitionTypeValidator.validateCreateRequest(request);

        String userOid = SecurityUtils.getCurrentUserOid(userService);
        request.setUserOid(userOid);

        AcquisitionType acquisitionType = acquisitionTypeMapper.toEntity(request);
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
        
        acquisitionTypeMapper.updateEntityFromRequest(existingAcquisitionType, request);
        AcquisitionType updatedAcquisitionType = acquisitionTypeRepository.save(existingAcquisitionType);
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
            throw new IllegalArgumentException(AqcisitionTypeConstants.NOT_FOUND_ERROR);
        }

        if (existingAcquisitionType.getIsDeleted()) {
            throw new IllegalArgumentException(AqcisitionTypeConstants.ALREADY_DELETED_ERROR);
        }
        
        existingAcquisitionType.setIsActive(false);
        existingAcquisitionType.setIsDeleted(true);
        existingAcquisitionType.setUpdatedDate(java.time.LocalDateTime.now());

        AcquisitionType deletedAcquisitionType = acquisitionTypeRepository.save(existingAcquisitionType);
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
