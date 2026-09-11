package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateSaleRequest;
import com.contatodo.application.dto.response.SaleResponse;
import com.contatodo.application.mapper.SaleMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.validators.SaleValidator;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.Sale;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.model.Money;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.domain.repositories.SaleRepository;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.shared.constants.SaleConstants;
import com.contatodo.shared.exceptions.ProductNotFoundException;
import com.contatodo.shared.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Use case service containing sale business logic.
 */
@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SaleValidator saleValidator;
    private final SaleMapper saleMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    /**
     * Creates a sale service.
     *
     * @param saleRepository Sale repository port.
     * @param productRepository Product repository port.
     * @param userRepository User repository port.
     * @param saleValidator Sale validator.
     * @param saleMapper Sale mapper.
     * @param authenticatedUserProvider Authenticated user provider.
     */
    public SaleService(
            SaleRepository saleRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            SaleValidator saleValidator,
            SaleMapper saleMapper,
            AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.saleValidator = saleValidator;
        this.saleMapper = saleMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    /**
     * Creates a new sale.
     *
     * @param request Create sale request.
     * @return Created sale response.
     */
    public SaleResponse createSale(CreateSaleRequest request) {
        saleValidator.validateCreateRequest(request);

        String userOid = authenticatedUserProvider.getCurrentUserOid();
        User user = userRepository.findById(userOid)
                .orElseThrow(() -> new UserNotFoundException(SaleConstants.USER_NOT_FOUND));

        Product product = productRepository.findById(request.getProductOid())
                .orElseThrow(() -> new ProductNotFoundException(SaleConstants.PRODUCT_NOT_FOUND));

        String productName = product.getName();
        Product updatedProduct = product.decreaseStock(request.getQuantity());

        Money totalCost = unitCostOf(product).multiply(request.getQuantity());
        Money originalTotalPrice = listUnitPriceOf(product).multiply(request.getQuantity());

        Long saleNumber = generateDailySaleNumber(userOid);

        Sale sale = Sale.place(
                saleNumber,
                request.getProductOid(),
                productName,
                user.getId(),
                request.getQuantity(),
                request.getTotalSalePrice(),
                totalCost,
                originalTotalPrice,
                request.getNotes()
        );

        productRepository.save(updatedProduct);
        Sale savedSale = saleRepository.save(sale);
        return saleMapper.toResponse(savedSale);
    }

    /**
     * Retrieves today's sales for the authenticated user.
     *
     * @return List of sale responses.
     */
    public List<SaleResponse> getTodaySales() {
        String userOid = authenticatedUserProvider.getCurrentUserOid();

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
        String userOid = authenticatedUserProvider.getCurrentUserOid();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Sale> sales = saleRepository.findByUserOidAndSaleDateBetween(userOid, startDateTime, endDateTime);
        return saleMapper.toResponseList(sales);
    }

    /**
     * Resolves the effective real unit cost of a product.
     *
     * @param product Source product.
     * @return Unit cost as money.
     */
    private Money unitCostOf(Product product) {
        Double unitRealCost = product.getUnitRealCost() != null ? product.getUnitRealCost() : product.getRealCost();
        return Money.of(unitRealCost);
    }

    /**
     * Resolves the effective list unit price of a product.
     *
     * @param product Source product.
     * @return Unit price as money.
     */
    private Money listUnitPriceOf(Product product) {
        Double unitPublicCost = product.getUnitPublicCost() != null ? product.getUnitPublicCost() : product.getRealCost();
        return Money.of(unitPublicCost);
    }

    /**
     * Generates a daily sale number for the given user.
     *
     * @param userOid Owner of the sales.
     * @return Next available sale number for today.
     */
    private Long generateDailySaleNumber(String userOid) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        Optional<Long> highestSaleNumber = saleRepository.findByUserOidAndSaleDateBetween(userOid, startOfDay, endOfDay)
                .stream()
                .map(Sale::getSaleNumber)
                .max(Long::compareTo);
        return highestSaleNumber.map(number -> number + 1).orElse(1L);
    }
}
