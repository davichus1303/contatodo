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
public class AcquisitionTypeMapper implements ResponseMapper<AcquisitionType, AcquisitionTypeResponse> {

    /**
     * Maps a create request to a domain entity.
     *
     * @param request Create acquisition type request.
     * @param userOid Authenticated owner identifier.
     * @return Acquisition type entity.
     */
    public AcquisitionType toEntity(CreateAcquisitionTypeRequest request, String userOid) {
        LocalDateTime now = LocalDateTime.now();
        return AcquisitionType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .userOid(userOid)
                .affectsInventory(request.getAffectsInventory() != null ? request.getAffectsInventory() : false)
                .isActive(true)
                .isDeleted(false)
                .createdDate(now)
                .updatedDate(now)
                .build();
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
     * Updates an existing acquisition type entity from an update request.
     * Only updates editable fields (name, description, isActive, and affectsInventory).
     *
     * @param acquisitionType Existing acquisition type entity.
     * @param request Update acquisition type request.
     */
    public AcquisitionType updateEntityFromRequest(AcquisitionType existing, UpdateAcquisitionTypeRequest request) {
        LocalDateTime now = LocalDateTime.now();
        return AcquisitionType.builder()
                .id(existing.getId())
                .name(request.getName() != null ? request.getName() : existing.getName())
                .description(request.getDescription() != null ? request.getDescription() : existing.getDescription())
                .userOid(existing.getUserOid())
                .isActive(request.getIsActive() != null ? request.getIsActive() : existing.getIsActive())
                .isDeleted(existing.getIsDeleted())
                .affectsInventory(request.getAffectsInventory() != null ? request.getAffectsInventory() : existing.getAffectsInventory())
                .createdDate(existing.getCreatedDate())
                .updatedDate(now)
                .build();
    }
}
