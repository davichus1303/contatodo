package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateSaleRequest;
import com.contatodo.application.dto.response.SaleResponse;
import com.contatodo.application.mapper.SaleMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.application.validators.SaleValidator;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.Sale;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.model.Money;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.domain.repositories.SaleRepository;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.constants.SaleConstants;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import com.contatodo.shared.utils.DateUtils;
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
    private final CompanyContextProvider companyContextProvider;

    /**
     * Creates a sale service.
     *
     * @param saleRepository Sale repository port.
     * @param productRepository Product repository port.
     * @param userRepository User repository port.
     * @param saleValidator Sale validator.
     * @param saleMapper Sale mapper.
     * @param authenticatedUserProvider Authenticated user provider.
     * @param companyContextProvider Company context provider.
     */
    public SaleService(
            SaleRepository saleRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            SaleValidator saleValidator,
            SaleMapper saleMapper,
            AuthenticatedUserProvider authenticatedUserProvider,
            CompanyContextProvider companyContextProvider
    ) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.saleValidator = saleValidator;
        this.saleMapper = saleMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.companyContextProvider = companyContextProvider;
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
                .orElseThrow(() -> new ResourceNotFoundException(SaleConstants.USER_NOT_FOUND));

        Product product = productRepository.findById(request.getProductOid())
                .orElseThrow(() -> new ResourceNotFoundException(SaleConstants.PRODUCT_NOT_FOUND));

        String productName = product.getName();
        Product updatedProduct = product.decreaseStock(request.getQuantity());

        Money unitRealCost = Money.of(product.getUnitRealCost() != null ? product.getUnitRealCost() : product.getRealCost());
        Money unitPublicCost = Money.of(product.getUnitPublicCost() != null ? product.getUnitPublicCost() : product.getRealCost());

        Long saleNumber = generateDailySaleNumber();

        Sale sale = Sale.place(
                saleNumber,
                request.getProductOid(),
                productName,
                user.getId(),
                request.getQuantity(),
                request.getTotalSalePrice(),
                unitRealCost,
                unitPublicCost,
                request.getNotes(),
                resolveCompanyOid()
        );

        productRepository.save(updatedProduct);
        Sale savedSale = saleRepository.save(sale);
        return saleMapper.toResponse(savedSale);
    }

    /**
     * Retrieves today's sales.
     *
     * @return List of sale responses.
     */
    public List<SaleResponse> getTodaySales() {
        LocalDate today = LocalDate.now();
        List<Sale> sales = saleRepository.findBySaleDate(today);
        return saleMapper.toResponseList(sales);
    }

    /**
     * Retrieves sales by date range.
     *
     * @param startDate Start date.
     * @param endDate End date.
     * @return List of sale responses.
     */
    public List<SaleResponse> getSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = DateUtils.startOfDay(startDate);
        LocalDateTime endDateTime = DateUtils.endOfDay(endDate);

        List<Sale> sales = saleRepository.findBySaleDateBetween(startDateTime, endDateTime);
        return saleMapper.toResponseList(sales);
    }

    /**
     * Generates the next daily sale number for the current company.
     *
     * @return Next available sale number for today.
     */
    private Long generateDailySaleNumber() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = DateUtils.startOfDay(today);
        LocalDateTime endOfDay = DateUtils.endOfDay(today);

        Optional<Long> highestSaleNumber = saleRepository.findBySaleDateBetween(startOfDay, endOfDay)
                .stream()
                .map(Sale::getSaleNumber)
                .max(Long::compareTo);
        return highestSaleNumber.map(number -> number + 1).orElse(1L);
    }

    /**
     * Resolves the owning company for a non-root write.
     *
     * @return Company identifier, or {@code null} for the root user.
     */
    private CompanyOid resolveCompanyOid() {
        if (companyContextProvider.isRoot()) {
            return null;
        }
        return companyContextProvider.currentCompanyOid()
                .orElseThrow(() -> new ResourceNotFoundException(AuthConstants.COMPANY_CONTEXT_REQUIRED));
    }
}
