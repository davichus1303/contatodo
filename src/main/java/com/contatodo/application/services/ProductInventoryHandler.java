package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateAcquisitionRequest;
import com.contatodo.domain.entities.Acquisition;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.ProductCostHistory;
import com.contatodo.domain.model.Money;
import com.contatodo.domain.repositories.AcquisitionRepository;
import com.contatodo.domain.repositories.ProductCostHistoryRepository;
import com.contatodo.domain.repositories.ProductRepository;
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
 * stay a thin orchestrator.</p>
 */
@Component
public class ProductInventoryHandler {

    private static final Logger log = LoggerFactory.getLogger(ProductInventoryHandler.class);

    private final ProductRepository productRepository;
    private final AcquisitionRepository acquisitionRepository;
    private final ProductCostHistoryRepository productCostHistoryRepository;

    /**
     * Creates an inventory handler.
     *
     * @param productRepository Product repository port.
     * @param acquisitionRepository Acquisition repository port.
     * @param productCostHistoryRepository Product cost history repository port.
     */
    public ProductInventoryHandler(
            ProductRepository productRepository,
            AcquisitionRepository acquisitionRepository,
            ProductCostHistoryRepository productCostHistoryRepository
    ) {
        this.productRepository = productRepository;
        this.acquisitionRepository = acquisitionRepository;
        this.productCostHistoryRepository = productCostHistoryRepository;
    }

    /**
     * Outcome of applying the inventory effects of one acquisition.
     *
     * @param productOid Affected or created product identifier.
     * @param productName Product name.
     * @param averageUnitRealCost Weighted average unit real cost after the acquisition.
     */
    public record InventoryOutcome(String productOid, String productName, Double averageUnitRealCost) {
    }

    /**
     * Creates or replenishes the acquired product and returns its identity
     * plus the recalculated weighted average unit real cost.
     *
     * @param request Create acquisition request.
     * @param userOid Owner of the product.
     * @return Outcome describing the affected product.
     */
    public InventoryOutcome applyToInventory(CreateAcquisitionRequest request, String userOid) {
        Product existingProduct = productRepository
                .findByNameAndUserOid(request.getProductName(), userOid)
                .orElse(null);

        if (existingProduct == null) {
            return createProductFromAcquisition(request, userOid);
        }
        return replenishExistingProduct(existingProduct, request);
    }

    /**
     * Records a product cost history entry for the persisted acquisition.
     *
     * @param productOid Product identifier.
     * @param request Create acquisition request.
     * @param savedAcquisition Persisted acquisition.
     * @param averageUnitRealCost Weighted average unit real cost.
     */
    public void recordCostHistory(
            String productOid,
            CreateAcquisitionRequest request,
            Acquisition savedAcquisition,
            Double averageUnitRealCost
    ) {
        productCostHistoryRepository.save(buildProductCostHistory(productOid, request, savedAcquisition, averageUnitRealCost));
    }

    /**
     * Replenishes an existing product with the acquired quantity and costs.
     *
     * @param existingProduct Product found by name and owner.
     * @param request Create acquisition request.
     * @return Outcome describing the updated product.
     */
    private InventoryOutcome replenishExistingProduct(Product existingProduct, CreateAcquisitionRequest request) {
        Double averageUnitRealCost = calculateAverageUnitRealCostIncludingNew(
                existingProduct.getId(),
                request.getRealCost(),
                request.getQuantity()
        );
        Product updatedProduct = existingProduct
                .replenish(request.getQuantity())
                .withAverageUnitRealCost(averageUnitRealCost)
                .withUnitPublicCost(request.getUnitPublicCost());
        productRepository.save(updatedProduct);

        return new InventoryOutcome(updatedProduct.getId(), updatedProduct.getName(), averageUnitRealCost);
    }

    /**
     * Creates a brand-new product from the acquisition data.
     *
     * @param request Create acquisition request.
     * @param userOid Owner of the product.
     * @return Outcome describing the created product.
     */
    private InventoryOutcome createProductFromAcquisition(CreateAcquisitionRequest request, String userOid) {
        Double averageUnitRealCost = Money.of(request.getRealCost()).divide(request.getQuantity()).toDouble();
        Product savedProduct = productRepository.save(buildProduct(request, userOid, averageUnitRealCost));
        return new InventoryOutcome(savedProduct.getId(), savedProduct.getName(), averageUnitRealCost);
    }

    /**
     * Calculates the weighted average unit real cost including a new
     * acquisition on top of all previous ones.
     *
     * @param productOid The product OID.
     * @param newRealCost The real cost of the new acquisition.
     * @param newQuantity The quantity of the new acquisition.
     * @return The weighted average unit real cost.
     */
    private Double calculateAverageUnitRealCostIncludingNew(String productOid, Double newRealCost, Integer newQuantity) {
        List<Acquisition> existingAcquisitions = acquisitionRepository.findByProductOid(productOid);

        Money totalCost = Money.of(newRealCost);
        int totalQuantity = newQuantity;

        for (Acquisition acquisition : existingAcquisitions) {
            totalCost = totalCost.add(Money.of(acquisition.getRealCost()));
            totalQuantity += acquisition.getQuantity();
        }

        return totalCost.divide(totalQuantity).toDouble();
    }

    /**
     * Builds a new product from the acquisition request.
     *
     * @param request The acquisition request containing product information.
     * @param userOid The OID of the user creating the product.
     * @param averageUnitRealCost Unit real cost derived from the acquisition.
     * @return A new product entity ready to persist.
     */
    private Product buildProduct(CreateAcquisitionRequest request, String userOid, Double averageUnitRealCost) {
        LocalDateTime now = LocalDateTime.now();
        return Product.builder()
                .name(request.getProductName())
                .description(request.getDescription() != null ? request.getDescription() : "")
                .stock(request.getQuantity())
                .code(generateNextProductCode())
                .realCost(request.getRealCost())
                .unitRealCost(averageUnitRealCost)
                .unitPublicCost(request.getUnitPublicCost())
                .isActive(true)
                .userOid(userOid)
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
     * @param averageUnitRealCost Weighted average unit real cost.
     * @return A new history entry ready to persist.
     */
    private ProductCostHistory buildProductCostHistory(
            String productOid,
            CreateAcquisitionRequest request,
            Acquisition savedAcquisition,
            Double averageUnitRealCost
    ) {
        return ProductCostHistory.builder()
                .productOid(productOid)
                .acquisitionOid(savedAcquisition.getId())
                .quantity(request.getQuantity())
                .remainingQuantity(request.getQuantity())
                .realCost(request.getRealCost())
                .unitRealCost(averageUnitRealCost)
                .unitPublicCostAtPurchase(request.getUnitPublicCost())
                .acquisitionDate(savedAcquisition.getAcquisitionDate())
                .userOid(savedAcquisition.getUserOid())
                .createdDate(LocalDateTime.now())
                .build();
    }

    /**
     * Generates the next sequential product code scanning existing codes.
     *
     * @return Next product code.
     */
    private String generateNextProductCode() {
        int maxCode = productRepository.findAll().stream()
                .map(Product::getCode)
                .mapToInt(ProductInventoryHandler::parseNumericCode)
                .max()
                .orElse(0);
        return String.valueOf(maxCode + 1);
    }

    /**
     * Parses a numeric product code ignoring malformed values.
     *
     * @param code Raw code.
     * @return Numeric value or zero when not parseable.
     */
    private static int parseNumericCode(String code) {
        try {
            return Integer.parseInt(code);
        } catch (NumberFormatException exception) {
            log.debug("Skipping non-numeric product code: {}", code);
            return 0;
        }
    }
}
