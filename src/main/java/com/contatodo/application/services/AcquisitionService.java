package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateAcquisitionRequest;
import com.contatodo.application.dto.response.AcquisitionResponse;
import com.contatodo.application.mapper.AcquisitionMapper;
import com.contatodo.application.mapper.ProductMapper;
import com.contatodo.application.validators.AcquisitionValidator;
import com.contatodo.domain.entities.Acquisition;
import com.contatodo.domain.entities.AcquisitionType;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.ProductCostHistory;
import com.contatodo.domain.repositories.AcquisitionRepository;
import com.contatodo.domain.repositories.AcquisitionTypeRepository;
import com.contatodo.domain.repositories.ProductCostHistoryRepository;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.shared.exceptions.ProductNotFoundException;
import com.contatodo.shared.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service containing acquisition business logic.
 */
@Service
public class AcquisitionService {

    private final AcquisitionRepository acquisitionRepository;
    private final ProductRepository productRepository;
    private final AcquisitionTypeRepository acquisitionTypeRepository;
    private final ProductCostHistoryRepository productCostHistoryRepository;
    private final AcquisitionValidator acquisitionValidator;
    private final AcquisitionMapper acquisitionMapper;
    private final ProductMapper productMapper;
    private final UserService userService;

    /**
     * Creates an acquisition service.
     *
     * @param acquisitionRepository Acquisition repository port.
     * @param productRepository Product repository port.
     * @param acquisitionTypeRepository Acquisition type repository port.
     * @param productCostHistoryRepository Product cost history repository port.
     * @param acquisitionValidator Acquisition validator.
     * @param acquisitionMapper Acquisition mapper.
     * @param productMapper Product mapper.
     * @param userService User service.
     */
    public AcquisitionService(
            AcquisitionRepository acquisitionRepository,
            ProductRepository productRepository,
            AcquisitionTypeRepository acquisitionTypeRepository,
            ProductCostHistoryRepository productCostHistoryRepository,
            AcquisitionValidator acquisitionValidator,
            AcquisitionMapper acquisitionMapper,
            ProductMapper productMapper,
            UserService userService
    ) {
        this.acquisitionRepository = acquisitionRepository;
        this.productRepository = productRepository;
        this.acquisitionTypeRepository = acquisitionTypeRepository;
        this.productCostHistoryRepository = productCostHistoryRepository;
        this.acquisitionValidator = acquisitionValidator;
        this.acquisitionMapper = acquisitionMapper;
        this.productMapper = productMapper;
        this.userService = userService;
    }

    /**
     * Registers a new acquisition.
     * This operation is atomic - if any part fails, no changes are persisted.
     *
     * @param request Create acquisition request.
     * @return Created acquisition response.
     */
    @Transactional
    public AcquisitionResponse registerAcquisition(CreateAcquisitionRequest request) {
        acquisitionValidator.validateCreateRequest(request);
        String userOid = SecurityUtils.getCurrentUserOid(userService);
        // Search for product by name and user OID
        Product product = productRepository.findByNameAndUserOid(request.getProductName(), userOid)
            .orElse(null);
        String productOid;
        String productName;
        Double averageUnitRealCost;

        if (product != null) {
            // Product exists - update it
            productOid = product.getId();
            productName = product.getName();
            // Increase stock
            product.setStock(product.getStock() + request.getQuantity());
            // Update public cost
            product.setUnitPublicCost(request.getUnitPublicCost());
            product.setUpdatedDate(LocalDateTime.now());
            // Calculate average unit real cost including the new acquisition
            averageUnitRealCost = calculateAverageUnitRealCostIncludingNew(productOid, request.getRealCost(), request.getQuantity());
            // Update product costs with average
            product.setRealCost(averageUnitRealCost * product.getStock());
            product.setUnitRealCost(averageUnitRealCost);
            productRepository.save(product);
        } else {
            // Product does not exist - create it automatically
            Product newProduct = buildProductObject(request, userOid);
            Product savedProduct = productRepository.save(newProduct);
            productOid = savedProduct.getId();
            productName = savedProduct.getName();
            averageUnitRealCost = request.getRealCost() / request.getQuantity();
        }

        // Create acquisition with average unit real cost
        Acquisition acquisition = acquisitionMapper.toEntity(request, productOid, userOid, averageUnitRealCost);
        Acquisition savedAcquisition = acquisitionRepository.save(acquisition);
        
        // Create product cost history   
        ProductCostHistory costHistory = buildProductCostHistoryObject(productOid, request, savedAcquisition, averageUnitRealCost);
        productCostHistoryRepository.save(costHistory);
        // Get acquisition type name for response
        AcquisitionType acquisitionType = acquisitionTypeRepository.findById(request.getAcquisitionTypeOid())
                .orElse(new AcquisitionType());
        String acquisitionTypeName = acquisitionType.getName() != null ? acquisitionType.getName() : "Unknown";
        return acquisitionMapper.toResponse(savedAcquisition, productName, acquisitionTypeName);
    }

    /**
     * Calculates the average unit real cost for a product based on all its acquisitions.
     * 
     * @param productOid The product OID
     * @return The average unit real cost across all acquisitions
     */
    private Double calculateAverageUnitRealCost(String productOid) {
        List<Acquisition> acquisitions = acquisitionRepository.findByProductOid(productOid);
        
        if (acquisitions.isEmpty()) {
            return 0.0;
        }
        
        double totalCost = 0.0;
        int totalQuantity = 0;
        
        for (Acquisition acquisition : acquisitions) {
            totalCost += acquisition.getRealCost();
            totalQuantity += acquisition.getQuantity();
        }
        
        return totalQuantity > 0 ? totalCost / totalQuantity : 0.0;
    }

    /**
     * Calculates the average unit real cost for a product including a new acquisition.
     * 
     * @param productOid The product OID
     * @param newRealCost The real cost of the new acquisition
     * @param newQuantity The quantity of the new acquisition
     * @return The average unit real cost including the new acquisition
     */
    private Double calculateAverageUnitRealCostIncludingNew(String productOid, Double newRealCost, Integer newQuantity) {
        List<Acquisition> existingAcquisitions = acquisitionRepository.findByProductOid(productOid);
        
        double totalCost = newRealCost;
        int totalQuantity = newQuantity;
        
        for (Acquisition acquisition : existingAcquisitions) {
            totalCost += acquisition.getRealCost();
            totalQuantity += acquisition.getQuantity();
        }
        
        return totalQuantity > 0 ? totalCost / totalQuantity : 0.0;
    }

    /**
     * Builds a new Product object from the acquisition request.
     * 
     * @param request The acquisition request containing product information
     * @param userOid The OID of the user creating the product
     * @return A new Product entity with the specified properties
     */
    private Product buildProductObject(CreateAcquisitionRequest request, String userOid) {
        String nextCode = generateNextProductCode();
        Double unitRealCost = request.getRealCost() / request.getQuantity();
        
        Product newProduct = new Product();
        newProduct.setName(request.getProductName());
        newProduct.setDescription(request.getDescription() != null ? request.getDescription() : "");
        newProduct.setStock(request.getQuantity());
        newProduct.setCode(nextCode);
        newProduct.setRealCost(request.getRealCost());
        newProduct.setUnitRealCost(unitRealCost);
        newProduct.setUnitPublicCost(request.getUnitPublicCost());
        newProduct.setUrlPhoto(null);
        newProduct.setIsActive(true);
        newProduct.setUserOid(userOid);
        newProduct.setCreatedDate(LocalDateTime.now());
        newProduct.setUpdatedDate(LocalDateTime.now());

        return newProduct;
    }

    /**
     * Builds a new ProductCostHistory object from the product and acquisition.
     * 
     * @param productOid The product OID
     * @param request The acquisition request
     * @param savedAcquisition The saved acquisition entity
     * @param averageUnitRealCost The average unit real cost
     * @return A new ProductCostHistory entity with the specified properties
     */
    private ProductCostHistory buildProductCostHistoryObject(String productOid, CreateAcquisitionRequest request,
        Acquisition savedAcquisition, Double averageUnitRealCost) {
        String userOid = savedAcquisition.getUserOid();
        
        ProductCostHistory costHistory = new ProductCostHistory();
        costHistory.setProductOid(productOid);
        costHistory.setAcquisitionOid(savedAcquisition.getId());
        costHistory.setQuantity(request.getQuantity());
        costHistory.setRemainingQuantity(request.getQuantity());
        costHistory.setRealCost(request.getRealCost());
        costHistory.setUnitRealCost(averageUnitRealCost);
        costHistory.setUnitPublicCostAtPurchase(request.getUnitPublicCost());
        costHistory.setAcquisitionDate(savedAcquisition.getAcquisitionDate());
        costHistory.setUserOid(userOid);
        costHistory.setCreatedDate(LocalDateTime.now());

        return costHistory;
    }

    /**
     * Retrieves acquisitions for the authenticated user within a date range.
     * If no date range is provided, returns today's acquisitions.
     *
     * @param startDate Optional start date.
     * @param endDate Optional end date.
     * @return List of acquisition responses.
     */
    public List<AcquisitionResponse> getAcquisitions(LocalDateTime startDate, LocalDateTime endDate) {
        String userOid = SecurityUtils.getCurrentUserOid(userService);

        List<Acquisition> acquisitions;
        if (startDate != null && endDate != null) {
            acquisitions = acquisitionRepository.findByUserOidAndAcquisitionDateBetween(userOid, startDate, endDate);
        } else {
            // Default to today's acquisitions
            LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
            LocalDateTime todayEnd = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
            acquisitions = acquisitionRepository.findByUserOidAndAcquisitionDateBetween(userOid, todayStart, todayEnd);
        }

        // Enrich responses with product names and acquisition types
        Map<String, String> productNames = enrichProductNames(acquisitions);
        Map<String, String> acquisitionTypes = enrichAcquisitionTypes(acquisitions);

        return acquisitionMapper.toResponseList(acquisitions, productNames, acquisitionTypes);
    }

    /**
     * Generates the next product code.
     *
     * @return Next product code.
     */
    private String generateNextProductCode() {
        List<Product> allProducts = productRepository.findAll();
        int maxCode = 0;
        
        for (Product product : allProducts) {
            try {
                int code = Integer.parseInt(product.getCode());
                if (code > maxCode) {
                    maxCode = code;
                }
            } catch (NumberFormatException exception) {
                // Skip non-numeric codes
            }
        }
        
        return String.valueOf(maxCode + 1);
    }

    /**
     * Enriches acquisitions with product names.
     *
     * @param acquisitions List of acquisitions.
     * @return Map of product OIDs to product names.
     */
    private Map<String, String> enrichProductNames(List<Acquisition> acquisitions) {
        Map<String, String> productNames = new HashMap<>();
        for (Acquisition acquisition : acquisitions) {
            if (acquisition.getProductOid() != null && !productNames.containsKey(acquisition.getProductOid())) {
                productRepository.findById(acquisition.getProductOid())
                        .ifPresent(product -> productNames.put(product.getId(), product.getName()));
            }
        }
        return productNames;
    }

    /**
     * Enriches acquisitions with acquisition type names.
     *
     * @param acquisitions List of acquisitions.
     * @return Map of acquisition type OIDs to acquisition type names.
     */
    private Map<String, String> enrichAcquisitionTypes(List<Acquisition> acquisitions) {
        Map<String, String> acquisitionTypes = new HashMap<>();
        for (Acquisition acquisition : acquisitions) {
            if (acquisition.getAcquisitionTypeOid() != null && !acquisitionTypes.containsKey(acquisition.getAcquisitionTypeOid())) {
                acquisitionTypeRepository.findById(acquisition.getAcquisitionTypeOid())
                        .ifPresent(type -> acquisitionTypes.put(type.getId(), type.getName()));
            }
        }
        return acquisitionTypes;
    }
}
