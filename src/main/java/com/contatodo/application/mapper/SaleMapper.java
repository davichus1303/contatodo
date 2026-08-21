package com.contatodo.application.mapper;

import com.contatodo.application.dto.response.SaleResponse;
import com.contatodo.domain.entities.Sale;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for sale responses.
 *
 * <p>Sale creation is handled by the domain factory {@link Sale#place(...)};
 * this mapper only translates entities to response DTOs.</p>
 */
@Component
public class SaleMapper {

    /**
     * Maps a sale entity to a response DTO.
     *
     * @param sale Sale entity.
     * @return Sale response.
     */
    public SaleResponse toResponse(Sale sale) {
        SaleResponse response = new SaleResponse();
        response.setId(sale.getId());
        response.setSaleNumber(sale.getSaleNumber());
        response.setProductOid(sale.getProductOid());
        response.setProductName(sale.getProductName());
        response.setUserOid(sale.getUserOid());
        response.setQuantity(sale.getQuantity());
        response.setTotalCost(sale.getTotalCost());
        response.setOriginalTotalPrice(sale.getOriginalTotalPrice());
        response.setTotalSalePrice(sale.getTotalSalePrice());
        response.setSaleDate(sale.getSaleDate());
        response.setNotes(sale.getNotes());
        response.setCreatedDate(sale.getCreatedDate());
        response.setUpdatedDate(sale.getUpdatedDate());
        return response;
    }

    /**
     * Maps a list of sales to response DTOs.
     *
     * @param sales Sale entities.
     * @return Sale responses.
     */
    public List<SaleResponse> toResponseList(List<Sale> sales) {
        return sales.stream().map(this::toResponse).toList();
    }
}
