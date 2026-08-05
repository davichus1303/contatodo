package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateAcquisitionTypeRequest;
import com.contatodo.application.dto.request.UpdateAcquisitionTypeRequest;
import com.contatodo.application.dto.response.AcquisitionTypeResponse;
import com.contatodo.domain.entities.AcquisitionType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper for acquisition type entities and DTOs.
 */
@Component
public class AcquisitionTypeMapper {

    /**
     * Maps a create request to a domain entity.
     *
     * @param request Create acquisition type request.
     * @return Acquisition type entity.
     */
    public AcquisitionType toEntity(CreateAcquisitionTypeRequest request) {
        AcquisitionType acquisitionType = new AcquisitionType();
        acquisitionType.setName(request.getName());
        acquisitionType.setDescription(request.getDescription());
        acquisitionType.setUserOid(request.getUserOid());
        acquisitionType.setAffectsInventory(request.getAffectsInventory() != null ? request.getAffectsInventory() : false);
        acquisitionType.setIsActive(true);
        acquisitionType.setIsDeleted(false);
        acquisitionType.setCreatedDate(LocalDateTime.now());
        acquisitionType.setUpdatedDate(LocalDateTime.now());
        return acquisitionType;
    }

    /**
     * Maps an acquisition type entity to a response DTO.
     *
     * @param acquisitionType Acquisition type entity.
     * @return Acquisition type response.
     */
    public AcquisitionTypeResponse toResponse(AcquisitionType acquisitionType) {
        AcquisitionTypeResponse response = new AcquisitionTypeResponse();
        response.setId(acquisitionType.getId());
        response.setName(acquisitionType.getName());
        response.setDescription(acquisitionType.getDescription());
        response.setUserOid(acquisitionType.getUserOid());
        response.setIsActive(acquisitionType.getIsActive());
        response.setIsDeleted(acquisitionType.getIsDeleted());
        response.setAffectsInventory(acquisitionType.getAffectsInventory());
        response.setCreatedDate(acquisitionType.getCreatedDate());
        response.setUpdatedDate(acquisitionType.getUpdatedDate());
        return response;
    }

    /**
     * Maps a list of acquisition types to response DTOs.
     *
     * @param acquisitionTypes Acquisition type entities.
     * @return Acquisition type responses.
     */
    public List<AcquisitionTypeResponse> toResponseList(List<AcquisitionType> acquisitionTypes) {
        return acquisitionTypes.stream().map(this::toResponse).toList();
    }

    /**
     * Updates an existing acquisition type entity from an update request.
     * Only updates editable fields (name, description, isActive, and affectsInventory).
     *
     * @param acquisitionType Existing acquisition type entity.
     * @param request Update acquisition type request.
     */
    public void updateEntityFromRequest(AcquisitionType acquisitionType, UpdateAcquisitionTypeRequest request) {
        if (request.getName() != null) {
            acquisitionType.setName(request.getName());
        }
        if (request.getDescription() != null) {
            acquisitionType.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            acquisitionType.setIsActive(request.getIsActive());
        }
        if (request.getAffectsInventory() != null) {
            acquisitionType.setAffectsInventory(request.getAffectsInventory());
        }
        acquisitionType.setUpdatedDate(LocalDateTime.now());
    }
}
