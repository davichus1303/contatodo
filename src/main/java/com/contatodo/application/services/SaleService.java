package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateSaleRequest;
import com.contatodo.application.dto.response.SaleResponse;
import com.contatodo.application.mapper.SaleMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.validators.SaleValidator;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.Sale;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.model.Money;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.domain.repositories.SaleRepository;
import com.contatodo.domain.repositories.UserRepository;
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
    private final CompanyService companyService;

    /**
     * Creates a sale service.
     *
     * @param saleRepository Sale repository port.
     * @param productRepository Product repository port.
     * @param userRepository User repository port.
     * @param saleValidator Sale validator.
     * @param saleMapper Sale mapper.
     * @param authenticatedUserProvider Authenticated user provider.
     * @param companyService Company service used to resolve the owning company.
     */
    public SaleService(
            SaleRepository saleRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            SaleValidator saleValidator,
            SaleMapper saleMapper,
            AuthenticatedUserProvider authenticatedUserProvider,
            CompanyService companyService
    ) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.saleValidator = saleValidator;
        this.saleMapper = saleMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.companyService = companyService;
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

        CompanyOid companyOid = companyService.resolveCompanyOid(request.getCompanyOid());
        Long saleNumber = generateDailySaleNumber(companyOid);

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
                companyOid
        );

        productRepository.save(updatedProduct);
        Sale savedSale = saleRepository.save(sale);
        return saleMapper.toResponse(savedSale);
    }

    /**
     * Retrieves today's sales.
     *
     * <p>The owning company comes from the session first; when the session has
     * none, the supplied company identifier is used and validated.</p>
     *
     * @param requestedCompanyOid Company identifier from the request, used when the session has none.
     * @return List of sale responses.
     */
    public List<SaleResponse> getTodaySales(String requestedCompanyOid) {
        CompanyOid companyOid = companyService.resolveReadCompanyOid(requestedCompanyOid);
        LocalDate today = LocalDate.now();
        List<Sale> sales = saleRepository.findBySaleDate(companyOid, today);
        return saleMapper.toResponseList(sales);
    }

    /**
     * Retrieves sales by date range.
     *
     * <p>The owning company comes from the session first; when the session has
     * none, the supplied company identifier is used and validated.</p>
     *
     * @param requestedCompanyOid Company identifier from the request, used when the session has none.
     * @param startDate Start date.
     * @param endDate End date.
     * @return List of sale responses.
     */
    public List<SaleResponse> getSalesByDateRange(String requestedCompanyOid, LocalDate startDate, LocalDate endDate) {
        CompanyOid companyOid = companyService.resolveReadCompanyOid(requestedCompanyOid);
        LocalDateTime startDateTime = DateUtils.startOfDay(startDate);
        LocalDateTime endDateTime = DateUtils.endOfDay(endDate);

        List<Sale> sales = saleRepository.findBySaleDateBetween(companyOid, startDateTime, endDateTime);
        return saleMapper.toResponseList(sales);
    }

    /**
     * Generates the next daily sale number for the current company.
     *
     * @return Next available sale number for today.
     */
    private Long generateDailySaleNumber(CompanyOid companyOid) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = DateUtils.startOfDay(today);
        LocalDateTime endOfDay = DateUtils.endOfDay(today);

        Optional<Long> highestSaleNumber = saleRepository.findBySaleDateBetween(companyOid, startOfDay, endOfDay)
                .stream()
                .map(Sale::getSaleNumber)
                .max(Long::compareTo);
        return highestSaleNumber.map(number -> number + 1).orElse(1L);
    }

    

    
}
