package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateSaleRequest;
import com.contatodo.application.dto.response.SaleResponse;
import com.contatodo.domain.entities.Sale;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper for sale entities and DTOs.
 */
@Component
public class SaleMapper {

    /**
     * Maps a create request to a domain entity.
     *
     * @param request Create sale request.
     * @param userOid Authenticated user identifier.
     * @param saleNumber Generated sale number.
     * @param totalCost Calculated total cost.
     * @param originalTotalPrice Calculated original total price.
     * @param productName Product name at the time of sale.
     * @return Sale entity.
     */
    public Sale toEntity(CreateSaleRequest request, String userOid, Long saleNumber, Double totalCost, Double originalTotalPrice, String productName) {
        Sale sale = new Sale();
        sale.setProductOid(request.getProductOid());
        sale.setProductName(productName);
        sale.setUserOid(userOid);
        sale.setQuantity(request.getQuantity());
        sale.setTotalCost(totalCost);
        sale.setOriginalTotalPrice(originalTotalPrice);
        sale.setTotalSalePrice(request.getTotalSalePrice());
        sale.setSaleNumber(saleNumber);
        sale.setSaleDate(LocalDateTime.now());
        sale.setNotes(request.getNotes());
        sale.setCreatedDate(LocalDateTime.now());
        sale.setUpdatedDate(LocalDateTime.now());
        sale.setIsDeleted(false);
        return sale;
    }

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
