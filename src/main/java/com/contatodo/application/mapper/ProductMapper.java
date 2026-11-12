package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateProductRequest;
import com.contatodo.application.dto.request.UpdateProductRequest;
import com.contatodo.application.dto.response.ProductResponse;
import com.contatodo.application.dto.response.UnitOfMeasureDetailResponse;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.UnitOfMeasure;
import com.contatodo.domain.model.CompanyOid;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Mapper for product entities and DTOs.
 */
@Component
public class ProductMapper implements ResponseMapper<Product, ProductResponse> {

    /**
     * Maps a create request to a domain entity.
     *
     * @param request Create product request.
     * @param code Generated product code.
     * @param byUserOid Owning user identifier.
     * @param companyOid Owning company identifier (null for root).
     * @return Product entity.
     */
    public Product toEntity(CreateProductRequest request, String code, String byUserOid, CompanyOid companyOid) {
        LocalDateTime now = LocalDateTime.now();
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .unitOfMeasures(request.getUnitOfMeasures())
                .stock(request.getStock())
                .code(code)
                .realCost(request.getRealCost())
                .unitRealCost(request.getUnitRealCost())
                .unitPublicCost(request.getUnitPublicCost())
                .urlPhoto(request.getUrlPhoto())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .byUserOid(byUserOid)
                .companyOid(companyOid)
                .createdDate(now)
                .updatedDate(now)
                .build();
    }

    /**
     * Applies update request changes to an existing product.
     *
     * @param product Existing product.
     * @param request Update product request.
     * @param updatedByUserOid Identifier of the user that last updated it.
     */
    public Product applyUpdate(Product existing, UpdateProductRequest request, String updatedByUserOid) {
        LocalDateTime now = LocalDateTime.now();
        return Product.builder()
                .id(existing.getId())
                .name(request.getName() != null ? request.getName() : existing.getName())
                .description(request.getDescription() != null ? request.getDescription() : existing.getDescription())
                .unitOfMeasures(request.getUnitOfMeasures() != null && !request.getUnitOfMeasures().isEmpty()
                        ? request.getUnitOfMeasures()
                        : existing.getUnitOfMeasures())
                .stock(request.getStock() != null ? request.getStock() : existing.getStock())
                .code(existing.getCode())
                .realCost(request.getRealCost() != null ? request.getRealCost() : existing.getRealCost())
                .unitRealCost(request.getUnitRealCost() != null ? request.getUnitRealCost() : existing.getUnitRealCost())
                .unitPublicCost(request.getUnitPublicCost() != null ? request.getUnitPublicCost() : existing.getUnitPublicCost())
                .urlPhoto(request.getUrlPhoto() != null ? request.getUrlPhoto() : existing.getUrlPhoto())
                .isActive(request.getIsActive() != null ? request.getIsActive() : existing.getIsActive())
                .byUserOid(existing.getByUserOid())
                .updatedByUserOid(updatedByUserOid)
                .companyOid(existing.getCompanyOid())
                .createdDate(existing.getCreatedDate())
                .updatedDate(now)
                .build();
    }

    /**
     * Maps a product entity to a response DTO.
     *
     * @param product Product entity.
     * @return Product response.
     */
    public ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setUnitOfMeasures(product.getUnitOfMeasures());
        response.setStock(product.getStock());
        response.setCode(product.getCode());
        response.setRealCost(product.getRealCost());
        response.setUnitRealCost(product.getUnitRealCost());
        response.setUnitPublicCost(product.getUnitPublicCost());
        response.setUrlPhoto(product.getUrlPhoto());
        response.setIsActive(product.getIsActive());
        response.setByUserOid(product.getByUserOid());
        response.setUpdatedByUserOid(product.getUpdatedByUserOid());
        return response;
    }

    /**
     * Maps products to responses joining the names of their units of measure.
     *
     * <p>Only units present in the supplied map are included, emulating an
     * inner join between the product unit identifiers and the unit catalog.</p>
     *
     * @param products Product entities.
     * @param unitsByOid Units of measure keyed by identifier.
     * @return Product responses with joined unit of measure details.
     */
    public List<ProductResponse> toResponseListWithUnitOfMeasureDetails(
            List<Product> products,
            Map<String, UnitOfMeasure> unitsByOid
    ) {
        return products.stream()
                .map(product -> toResponseWithUnitOfMeasureDetails(product, unitsByOid))
                .toList();
    }

    /**
     * Maps a product to a response joining the names of its units of measure.
     *
     * @param product Product entity.
     * @param unitsByOid Units of measure keyed by identifier.
     * @return Product response with joined unit of measure details.
     */
    public ProductResponse toResponseWithUnitOfMeasureDetails(
            Product product,
            Map<String, UnitOfMeasure> unitsByOid
    ) {
        ProductResponse response = toResponse(product);
        List<UnitOfMeasureDetailResponse> details = product.getUnitOfMeasures().stream()
                .filter(Objects::nonNull)
                .map(unitsByOid::get)
                .filter(Objects::nonNull)
                .map(unit -> new UnitOfMeasureDetailResponse(unit.getId(), unit.getName()))
                .toList();
        response.setUnitOfMeasureDetails(details);
        return response;
    }
}
