package com.contatodo.application.mapper;

import com.contatodo.application.dto.response.SaleResponse;
import com.contatodo.domain.entities.Sale;
import com.contatodo.domain.entities.UnitOfMeasure;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Mapper for sale responses.
 *
 * <p>Sale creation is handled by the domain factory {@link Sale#place(...)};
 * this mapper only translates entities to response DTOs.</p>
 */
@Component
public class SaleMapper implements ResponseMapper<Sale, SaleResponse> {

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
        response.setByUserOid(sale.getByUserOid());
        response.setQuantity(sale.getQuantity());
        response.setUnitOfMeasureOid(sale.getUnitOfMeasureOid());
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
     * Maps sales to responses joining the name of their unit of measure.
     *
     * <p>Only units present in the supplied map are joined, emulating an inner
     * join between the sale unit identifier and the unit catalog. A legacy sale
     * without a unit, or one referencing an inactive or missing unit, keeps a
     * {@code null} name.</p>
     *
     * @param sales Sale entities.
     * @param unitsByOid Units of measure keyed by identifier.
     * @return Sale responses with the joined unit of measure name.
     */
    public List<SaleResponse> toResponseListWithUnitOfMeasureNames(
            List<Sale> sales,
            Map<String, UnitOfMeasure> unitsByOid
    ) {
        return sales.stream()
                .map(sale -> toResponseWithUnitOfMeasureName(sale, unitsByOid))
                .toList();
    }

    /**
     * Maps a sale to a response joining the name of its unit of measure.
     *
     * @param sale Sale entity.
     * @param unitsByOid Units of measure keyed by identifier.
     * @return Sale response with the joined unit of measure name.
     */
    public SaleResponse toResponseWithUnitOfMeasureName(Sale sale, Map<String, UnitOfMeasure> unitsByOid) {
        SaleResponse response = toResponse(sale);
        response.setUnitOfMeasureName(
                Optional.ofNullable(sale.getUnitOfMeasureOid())
                        .map(unitsByOid::get)
                        .map(UnitOfMeasure::getName)
                        .orElse(null)
        );
        return response;
    }
}
