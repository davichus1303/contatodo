package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateUnitOfMeasureRequest;
import com.contatodo.application.dto.response.UnitOfMeasureResponse;
import com.contatodo.domain.entities.UnitOfMeasure;
import org.springframework.stereotype.Component;

/**
 * Mapper for unit of measure entities and DTOs.
 */
@Component
public class UnitOfMeasureMapper implements ResponseMapper<UnitOfMeasure, UnitOfMeasureResponse> {

    /**
     * Maps a create request to a domain entity.
     *
     * @param request Create unit of measure request.
     * @param companyOid Owning company identifier.
     * @return Unit of measure entity.
     */
    public UnitOfMeasure toEntity(CreateUnitOfMeasureRequest request, String companyOid) {
        return UnitOfMeasure.builder()
                .name(request.getName())
                .abrev(request.getAbrev())
                .companyOid(companyOid)
                .isActive(true)
                .isDeleted(false)
                .build();
    }

    /**
     * Maps a unit of measure entity to a response DTO.
     *
     * @param unitOfMeasure Unit of measure entity.
     * @return Unit of measure response.
     */
    public UnitOfMeasureResponse toResponse(UnitOfMeasure unitOfMeasure) {
        UnitOfMeasureResponse response = new UnitOfMeasureResponse();
        response.setId(unitOfMeasure.getId());
        response.setName(unitOfMeasure.getName());
        response.setAbrev(unitOfMeasure.getAbrev());
        response.setCompanyOid(unitOfMeasure.getCompanyOid());
        response.setIsActive(unitOfMeasure.getIsActive());
        response.setIsDeleted(unitOfMeasure.getIsDeleted());
        return response;
    }
}
