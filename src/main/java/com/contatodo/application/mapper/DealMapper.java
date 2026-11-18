package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateDealRequest;
import com.contatodo.application.dto.request.UpdateDealRequest;
import com.contatodo.application.dto.response.DealResponse;
import com.contatodo.domain.entities.Deal;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper for deal entities and DTOs.
 */
@Component
public class DealMapper implements ResponseMapper<Deal, DealResponse> {

    /**
     * Maps a create request to a domain entity.
     *
     * @param request Create deal request.
     * @param now Creation/update timestamp.
     * @return Deal entity.
     */
    public Deal toEntity(CreateDealRequest request, LocalDateTime now) {
        return Deal.builder()
                .productOid(request.getProductOid())
                .unitOfMeasureOid(request.getUnitOfMeasureOid())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .companyOid(request.getCompanyOid())
                .createdDate(now)
                .updateDate(now)
                .isActive(true)
                .isDelete(false)
                .build();
    }

    @Override
    public DealResponse toResponse(Deal deal) {
        DealResponse response = new DealResponse();
        response.setId(deal.getId());
        response.setProductOid(deal.getProductOid());
        response.setUnitOfMeasureOid(deal.getUnitOfMeasureOid());
        response.setQuantity(deal.getQuantity());
        response.setPrice(deal.getPrice());
        response.setCompanyOid(deal.getCompanyOid());
        response.setCreatedDate(deal.getCreatedDate());
        response.setUpdateDate(deal.getUpdateDate());
        response.setIsActive(deal.getIsActive());
        response.setIsDelete(deal.getIsDelete());
        return response;
    }

    /**
     * Applies allowed updates to an existing deal.
     *
     * @param existing Existing deal.
     * @param request Update request.
     * @param now Update timestamp.
     * @return Updated deal entity.
     */
    public Deal applyUpdate(Deal existing, UpdateDealRequest request, LocalDateTime now) {
        return Deal.builder()
                .id(existing.getId())
                .productOid(existing.getProductOid())
                .unitOfMeasureOid(existing.getUnitOfMeasureOid())
                .quantity(request.getQuantity() != null ? request.getQuantity() : existing.getQuantity())
                .price(request.getPrice() != null ? request.getPrice() : existing.getPrice())
                .companyOid(existing.getCompanyOid())
                .createdDate(existing.getCreatedDate())
                .updateDate(now)
                .isActive(request.getIsActive() != null ? request.getIsActive() : existing.getIsActive())
                .isDelete(request.getIsDelete() != null ? request.getIsDelete() : existing.getIsDelete())
                .build();
    }
}
