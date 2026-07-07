package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateSaleRequest;
import com.contatodo.application.dto.response.SaleResponse;
import com.contatodo.application.mapper.SaleMapper;
import com.contatodo.application.validators.SaleValidator;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.Sale;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.SaleRepository;
import com.contatodo.shared.constants.SaleConstants;
import com.contatodo.shared.exceptions.InsufficientStockException;
import com.contatodo.shared.exceptions.ProductNotFoundException;
import com.contatodo.shared.exceptions.SaleWithoutProfitException;
import com.contatodo.shared.exceptions.UserNotFoundException;
import com.contatodo.shared.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service containing sale business logic.
 */
@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleValidator saleValidator;
    private final SaleMapper saleMapper;
    private final UserService userService;

    /**
     * Creates a sale service.
     *
     * @param saleRepository Sale repository port.
     * @param saleValidator Sale validator.
     * @param saleMapper Sale mapper.
     * @param userService User service.
     */
    public SaleService(
            SaleRepository saleRepository,
            SaleValidator saleValidator,
            SaleMapper saleMapper,
            UserService userService
    ) {
        this.saleRepository = saleRepository;
        this.saleValidator = saleValidator;
        this.saleMapper = saleMapper;
        this.userService = userService;
    }

    /**
     * Creates a new sale.
     *
     * @param request Create sale request.
     * @return Created sale response.
     */
    public SaleResponse createSale(CreateSaleRequest request) {
        saleValidator.validateCreateRequest(request);

        String userOid = SecurityUtils.getCurrentUserOid(userService);
        User user = saleRepository.findUserById(userOid)
                .orElseThrow(() -> new UserNotFoundException(SaleConstants.USER_NOT_FOUND));

        Product product = saleRepository.findProductById(request.getProductOid())
                .orElseThrow(() -> new ProductNotFoundException(SaleConstants.PRODUCT_NOT_FOUND));

        String productName = product.getName();
        validateStock(product, request.getQuantity());

        Double totalCost = calculateTotalCost(product, request.getQuantity());
        Double originalTotalPrice = calculateOriginalTotalPrice(product, request.getQuantity());

        validateProfit(totalCost, request.getTotalSalePrice());

        Long saleNumber = generateDailySaleNumber();

        decreaseProductStock(product, request.getQuantity());
        saleRepository.updateProduct(product);

        Sale sale = saleMapper.toEntity(
                request,
                user.getId(),
                saleNumber,
                totalCost,
                originalTotalPrice,
                productName
        );
        Sale savedSale = saleRepository.save(sale);
        return saleMapper.toResponse(savedSale);
    }

    /**
     * Retrieves today's sales for the authenticated user.
     *
     * @return List of sale responses.
     */
    public List<SaleResponse> getTodaySales() {
        String userOid = SecurityUtils.getCurrentUserOid(userService);

        LocalDate today = LocalDate.now();
        List<Sale> sales = saleRepository.findByUserOidAndSaleDate(userOid, today);
        return saleMapper.toResponseList(sales);
    }

    /**
     * Retrieves sales for the authenticated user by date range.
     *
     * @param startDate Start date.
     * @param endDate End date.
     * @return List of sale responses.
     */
    public List<SaleResponse> getSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        String userOid = SecurityUtils.getCurrentUserOid(userService);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Sale> sales = saleRepository.findByUserOidAndSaleDateBetween(userOid, startDateTime, endDateTime);
        return saleMapper.toResponseList(sales);
    }

    private void validateStock(Product product, Integer quantity) {
        if (product.getStock() == null || product.getStock() <= 0) {
            throw new InsufficientStockException(SaleConstants.PRODUCT_OUT_OF_STOCK);
        }
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(SaleConstants.INSUFFICIENT_STOCK);
        }
    }

    private Double calculateTotalCost(Product product, Integer quantity) {
        Double unitCost = product.getUnitRealCost() != null ? product.getUnitRealCost() : product.getRealCost();
        return unitCost * quantity;
    }

    private Double calculateOriginalTotalPrice(Product product, Integer quantity) {
        Double unitPrice = product.getUnitPublicCost() != null ? product.getUnitPublicCost() : product.getRealCost();
        return unitPrice * quantity;
    }

    private void validateProfit(Double totalCost, Double totalSalePrice) {
        if (totalSalePrice <= totalCost) {
            throw new SaleWithoutProfitException(SaleConstants.SALE_WITHOUT_PROFIT);
        }
    }

    private Long generateDailySaleNumber() {
        String userOid = SecurityUtils.getCurrentUserOid(userService);
        
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        
        List<Sale> userSales = saleRepository.findByUserOidAndSaleDateBetween(userOid, startOfDay, endOfDay);
        return userSales.stream()
                .map(Sale::getSaleNumber)
                .max(Long::compareTo)
                .map(max -> max + 1)
                .orElse(1L);
    }

    private void decreaseProductStock(Product product, Integer quantity) {
        product.setStock(product.getStock() - quantity);
        product.setUpdatedDate(LocalDateTime.now());
    }
}
