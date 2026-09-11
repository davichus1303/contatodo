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
        LocalDateTime now = LocalDateTime.now();
        return Acquisition.builder()
                .acquisitionTypeOid(request.getAcquisitionTypeOid())
                .productOid(productOid)
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .realCost(request.getRealCost())
                .unitRealCost(unitRealCost)
                .unitPublicCost(request.getUnitPublicCost())
                .supplierOid(request.getSupplierOid())
                .supplierName(request.getSupplierName())
                .invoiceNumber(request.getInvoiceNumber())
                .observations(request.getObservations())
                .userOid(userOid)
                .acquisitionDate(now)
                .isDeleted(false)
                .createdDate(now)
                .updatedDate(now)
                .build();
    }

    /**
     * Maps an acquisition entity to a response DTO with enriched information.
     *
     * @param acquisition Acquisition entity.
     * @param productName Product name (if null, will use acquisition.getProductName()).
     * @param acquisitionType Acquisition type name.
     * @return Acquisition response.
     */
    public AcquisitionResponse toResponse(Acquisition acquisition, String productName, String acquisitionType) {
        AcquisitionResponse response = new AcquisitionResponse();
        response.setId(acquisition.getId());
        // Use the provided productName, or fall back to the entity's productName
        response.setProductName(productName != null ? productName : acquisition.getProductName());
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
                .map(acquisition -> {
                    // Use product name from map if productOid exists and is found, otherwise use entity's productName
                    String productName = null;
                    if (acquisition.getProductOid() != null && productNames.containsKey(acquisition.getProductOid())) {
                        productName = productNames.get(acquisition.getProductOid());
                    } else {
                        productName = acquisition.getProductName();
                    }
                    // Fallback to "Unknown" if both are null
                    if (productName == null) {
                        productName = "Unknown";
                    }
                    return toResponse(
                            acquisition,
                            productName,
                            acquisitionTypes.getOrDefault(acquisition.getAcquisitionTypeOid(), "Unknown")
                    );
                })
                .toList();
    }
}
