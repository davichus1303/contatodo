package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateAcquisitionRequest;
import com.contatodo.application.dto.response.AcquisitionResponse;
import com.contatodo.domain.entities.Acquisition;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper for acquisition entities and DTOs.
 */
@Component
public class AcquisitionMapper {

    /**
     * Maps a create request to a domain entity.
     *
     * @param request Create acquisition request.
     * @param productOid Product OID.
     * @param userOid User OID.
     * @param unitRealCost Calculated unit real cost.
     * @return Acquisition entity.
     */
    public Acquisition toEntity(CreateAcquisitionRequest request, String productOid, String userOid, Double unitRealCost) {
        Acquisition acquisition = new Acquisition();
        acquisition.setAcquisitionTypeOid(request.getAcquisitionTypeOid());
        acquisition.setProductOid(productOid);
        acquisition.setQuantity(request.getQuantity());
        acquisition.setRealCost(request.getRealCost());
        acquisition.setUnitRealCost(unitRealCost);
        acquisition.setUnitPublicCost(request.getUnitPublicCost());
        acquisition.setSupplierOid(request.getSupplierOid());
        acquisition.setSupplierName(request.getSupplierName());
        acquisition.setInvoiceNumber(request.getInvoiceNumber());
        acquisition.setObservations(request.getObservations());
        acquisition.setUserOid(userOid);
        acquisition.setAcquisitionDate(LocalDateTime.now());
        acquisition.setIsDeleted(false);
        acquisition.setCreatedDate(LocalDateTime.now());
        acquisition.setUpdatedDate(LocalDateTime.now());
        return acquisition;
    }

    /**
     * Maps an acquisition entity to a response DTO with enriched information.
     *
     * @param acquisition Acquisition entity.
     * @param productName Product name.
     * @param acquisitionType Acquisition type name.
     * @return Acquisition response.
     */
    public AcquisitionResponse toResponse(Acquisition acquisition, String productName, String acquisitionType) {
        AcquisitionResponse response = new AcquisitionResponse();
        response.setId(acquisition.getId());
        response.setProductName(productName);
        response.setAcquisitionType(acquisitionType);
        response.setQuantity(acquisition.getQuantity());
        response.setRealCost(acquisition.getRealCost());
        response.setUnitRealCost(acquisition.getUnitRealCost());
        response.setUnitPublicCost(acquisition.getUnitPublicCost());
        response.setSupplierName(acquisition.getSupplierName());
        response.setInvoiceNumber(acquisition.getInvoiceNumber());
        response.setAcquisitionDate(acquisition.getAcquisitionDate());
        response.setObservations(acquisition.getObservations());
        return response;
    }

    /**
     * Maps a list of acquisition entities to response DTOs with enriched information.
     *
     * @param acquisitions Acquisition entities.
     * @param productNames Map of product OIDs to product names.
     * @param acquisitionTypes Map of acquisition type OIDs to acquisition type names.
     * @return Acquisition responses.
     */
    public List<AcquisitionResponse> toResponseList(
            List<Acquisition> acquisitions,
            java.util.Map<String, String> productNames,
            java.util.Map<String, String> acquisitionTypes
    ) {
        return acquisitions.stream()
                .map(acquisition -> toResponse(
                        acquisition,
                        productNames.getOrDefault(acquisition.getProductOid(), "Unknown"),
                        acquisitionTypes.getOrDefault(acquisition.getAcquisitionTypeOid(), "Unknown")
                ))
                .toList();
    }
}
