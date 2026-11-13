package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateAcquisitionRequest;
import com.contatodo.application.validators.ProductValidator;
import com.contatodo.domain.entities.Acquisition;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.ProductCostHistory;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.model.Money;
import com.contatodo.domain.repositories.ProductCostHistoryRepository;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.shared.constants.AcquisitionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Applies the inventory side effects of an acquisition.
 *
 * <p>Encapsulates product creation or replenishment, weighted average cost
 * recalculation and cost history recording so the acquisition use case can
 * stay a thin orchestrator. Inventory quantities are always expressed in the
 * base unit of measure: when the acquisition is registered in a pack unit the
 * quantity is multiplied by the product pack size before touching stock.</p>
 */
@Component
public class ProductInventoryHandler {

    private static final Logger log = LoggerFactory.getLogger(ProductInventoryHandler.class);

    private final ProductRepository productRepository;
    private final ProductCostHistoryRepository productCostHistoryRepository;
    private final ProductValidator productValidator;
    private final PackConversionService packConversionService;

    /**
     * Creates an inventory handler.
     *
     * @param productRepository Product repository port.
     * @param productCostHistoryRepository Product cost history repository port.
     * @param productValidator Product validator reused for unit of measure rules.
     * @param packConversionService Shared pack to base unit converter.
     */
    public ProductInventoryHandler(
            ProductRepository productRepository,
            ProductCostHistoryRepository productCostHistoryRepository,
            ProductValidator productValidator,
            PackConversionService packConversionService
    ) {
        this.productRepository = productRepository;
        this.productCostHistoryRepository = productCostHistoryRepository;
        this.productValidator = productValidator;
        this.packConversionService = packConversionService;
    }

    /**
     * Outcome of applying the inventory effects of one acquisition.
     *
     * @param productOid Affected or created product identifier.
     * @param productName Product name.
     * @param averageUnitRealCost Weighted average unit real cost after the acquisition.
     * @param packSize Effective pack size used to convert the acquisition.
     * @param baseQuantity Quantity added to inventory in base units.
     */
    public record InventoryOutcome(
            String productOid,
            String productName,
            Double averageUnitRealCost,
            Integer packSize,
            Integer baseQuantity
    ) {
    }

    /**
     * Creates or replenishes the acquired product and returns its identity
     * plus the recalculated weighted average unit real cost.
     *
     * @param request Create acquisition request.
     * @param userOid Owner of the product.
     * @param companyOid Owning company identifier (null for root).
     * @param isPack Whether the acquired unit of measure represents a pack.
     * @return Outcome describing the affected product.
     */
    public InventoryOutcome applyToInventory(
            CreateAcquisitionRequest request,
            String userOid,
            CompanyOid companyOid,
            boolean isPack
    ) {
        Product existingProduct = productRepository
                .findByNameAndIsActiveTrue(request.getProductName())
                .orElse(null);

        Integer effectivePackSize = resolvePackSize(request.getPackSize(), existingProduct, isPack);
        int baseQuantity = packConversionService.toBaseUnits(
                request.getQuantity(),
                isPack,
                effectivePackSize,
                AcquisitionConstants.PACK_SIZE_REQUIRED
        );

        if (existingProduct == null) {
            productValidator.validateRequiredUnitOfMeasures(request.getUnitOfMeasures());
            return createProductFromAcquisition(request, userOid, companyOid, effectivePackSize, baseQuantity);
        }

        return replenishExistingProduct(existingProduct, request, effectivePackSize, baseQuantity);
    }

    /**
     * Records a product cost history entry for the persisted acquisition.
     *
     * @param productOid Product identifier.
     * @param request Create acquisition request.
     * @param savedAcquisition Persisted acquisition.
     * @param outcome Inventory outcome of the acquisition.
     */
    public void recordCostHistory(
            String productOid,
            CreateAcquisitionRequest request,
            Acquisition savedAcquisition,
            InventoryOutcome outcome
    ) {
        productCostHistoryRepository.save(
                buildProductCostHistory(productOid, request, savedAcquisition, outcome)
        );
    }

    /**
     * Resolves the pack size used to convert the acquisition.
     *
     * <p>The request value wins. When it is absent and the acquisition is
     * registered in a pack unit, the current product pack size is used so an
     * acquisition of an existing product can omit it. Acquisitions in base
     * units do not snapshot a pack size they never used.</p>
     *
     * @param requestedPackSize Pack size supplied in the request.
     * @param existingProduct Existing product, or null when it does not exist.
     * @param isPack Whether the acquired unit of measure represents a pack.
     * @return Effective pack size, or null when none is available or needed.
     */
    private Integer resolvePackSize(Integer requestedPackSize, Product existingProduct, boolean isPack) {
        if (requestedPackSize != null) {
            return requestedPackSize;
        }
        if (!isPack) {
            return null;
        }
        return existingProduct != null ? existingProduct.getPackSize() : null;
    }

    /**
     * Replenishes an existing product with the acquired quantity and costs.
     *
     * @param existingProduct Product found by name and owner.
     * @param request Create acquisition request.
     * @param packSize Effective pack size used to convert the acquisition.
     * @param baseQuantity Quantity added to inventory in base units.
     * @return Outcome describing the updated product.
     */
    private InventoryOutcome replenishExistingProduct(
            Product existingProduct,
            CreateAcquisitionRequest request,
            Integer packSize,
            Integer baseQuantity
    ) {
        Double averageUnitRealCost = calculateAverageUnitRealCostIncludingNew(
                existingProduct.getId(),
                request.getRealCost(),
                baseQuantity
        );
        Product updatedProduct = existingProduct
                .withPackSize(packSize)
                .replenish(baseQuantity)
                .withAverageUnitRealCost(averageUnitRealCost)
                .withUnitPublicCost(request.getUnitPublicCost());
        productRepository.save(updatedProduct);

        return new InventoryOutcome(
                updatedProduct.getId(),
                updatedProduct.getName(),
                averageUnitRealCost,
                packSize,
                baseQuantity
        );
    }

    /**
     * Creates a brand-new product from the acquisition data.
     *
     * @param request Create acquisition request.
     * @param userOid Owner of the product.
     * @param companyOid Owning company identifier (null for root).
     * @param packSize Effective pack size used to convert the acquisition.
     * @param baseQuantity Quantity added to inventory in base units.
     * @return Outcome describing the created product.
     */
    private InventoryOutcome createProductFromAcquisition(
            CreateAcquisitionRequest request,
            String userOid,
            CompanyOid companyOid,
            Integer packSize,
            Integer baseQuantity
    ) {
        Double averageUnitRealCost = Money.of(request.getRealCost()).divide(baseQuantity).toDouble();
        Product savedProduct = productRepository.save(
                buildProduct(request, userOid, companyOid, averageUnitRealCost, packSize, baseQuantity)
        );
        return new InventoryOutcome(
                savedProduct.getId(),
                savedProduct.getName(),
                averageUnitRealCost,
                packSize,
                baseQuantity
        );
    }

    /**
     * Calculates the weighted average unit real cost including a new
     * acquisition on top of all previous ones.
     *
     * <p>The cost history stores quantities in base units, so packs acquired
     * in the past are weighted correctly against the new base quantity.</p>
     *
     * @param productOid The product OID.
     * @param newRealCost The real cost of the new acquisition.
     * @param newBaseQuantity The base quantity of the new acquisition.
     * @return The weighted average unit real cost.
     */
    private Double calculateAverageUnitRealCostIncludingNew(
            String productOid,
            Double newRealCost,
            Integer newBaseQuantity
    ) {
        List<ProductCostHistory> existingHistory = productCostHistoryRepository.findByProductOid(productOid);

        Money totalCost = Money.of(newRealCost);
        int totalQuantity = newBaseQuantity;

        for (ProductCostHistory history : existingHistory) {
            if (history.getQuantity() == null || history.getQuantity() <= 0) {
                continue;
            }
            totalCost = totalCost.add(Money.of(history.getRealCost()));
            totalQuantity += history.getQuantity();
        }

        return totalCost.divide(totalQuantity).toDouble();
    }

    /**
     * Builds a new product from the acquisition request.
     *
     * @param request The acquisition request containing product information.
     * @param userOid The OID of the user creating the product.
     * @param companyOid Owning company identifier (null for root).
     * @param averageUnitRealCost Unit real cost derived from the acquisition.
     * @param packSize Effective pack size used to convert the acquisition.
     * @param baseQuantity Quantity added to inventory in base units.
     * @return A new product entity ready to persist.
     */
    private Product buildProduct(
            CreateAcquisitionRequest request,
            String userOid,
            CompanyOid companyOid,
            Double averageUnitRealCost,
            Integer packSize,
            Integer baseQuantity
    ) {
        LocalDateTime now = LocalDateTime.now();
        return Product.builder()
                .name(request.getProductName())
                .description(request.getDescription() != null ? request.getDescription() : "")
                .unitOfMeasures(request.getUnitOfMeasures())
                .packSize(packSize)
                .stock(baseQuantity)
                .code(generateNextProductCode(companyOid))
                .realCost(request.getRealCost())
                .unitRealCost(averageUnitRealCost)
                .unitPublicCost(request.getUnitPublicCost())
                .isActive(true)
                .byUserOid(userOid)
                .companyOid(companyOid)
                .createdDate(now)
                .updatedDate(now)
                .build();
    }

    /**
     * Builds a product cost history entry for the acquisition.
     *
     * @param productOid Product OID.
     * @param request Acquisition request.
     * @param savedAcquisition Persisted acquisition.
     * @param outcome Inventory outcome of the acquisition.
     * @return A new history entry ready to persist.
     */
    private ProductCostHistory buildProductCostHistory(
            String productOid,
            CreateAcquisitionRequest request,
            Acquisition savedAcquisition,
            InventoryOutcome outcome
    ) {
        String unitOfMeasureOid = savedAcquisition.getUnitOfMeasureOid() != null
                ? savedAcquisition.getUnitOfMeasureOid()
                : savedAcquisition.getUnitOfMeasure();
        return ProductCostHistory.builder()
                .productOid(productOid)
                .acquisitionOid(savedAcquisition.getId())
                .unitOfMeasureOid(unitOfMeasureOid)
                .packSize(outcome.packSize())
                .quantity(outcome.baseQuantity())
                .remainingQuantity(outcome.baseQuantity())
                .realCost(request.getRealCost())
                .unitRealCost(outcome.averageUnitRealCost())
                .unitPublicCostAtPurchase(request.getUnitPublicCost())
                .acquisitionDate(savedAcquisition.getAcquisitionDate())
                .byUserOid(savedAcquisition.getByUserOid())
                .companyOid(savedAcquisition.getCompanyOid())
                .createdDate(LocalDateTime.now())
                .build();
    }

    /**
     * Generates the next sequential product code scanning existing codes.
     *
     * @return Next product code.
     */
    private String generateNextProductCode(CompanyOid companyOid) {
        int maxCode = productRepository.findAll(companyOid).stream()
                .map(Product::getCode)
                .mapToInt(ProductInventoryHandler::parseNumericCode)
                .max()
                .orElse(0);
        return String.valueOf(maxCode + 1);
    }

    /**
     * Parses a numeric product code ignoring malformed values.
     *
     * @param code Product code.
     * @return Parsed numeric code, or zero when it cannot be parsed.
     */
    private static int parseNumericCode(String code) {
        try {
            return Integer.parseInt(code);
        } catch (NumberFormatException exception) {
            log.warn("Ignoring malformed product code: {}", code);
            return 0;
        }
    }
}
