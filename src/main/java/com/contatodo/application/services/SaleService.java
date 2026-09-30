package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateSaleRequest;
import com.contatodo.application.dto.response.SaleResponse;
import com.contatodo.application.mapper.SaleMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.application.validators.CompanyOidValidator;
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
    private final CompanyOidValidator companyOidValidator;

    /**
     * Creates a sale service.
     *
     * @param saleRepository Sale repository port.
     * @param productRepository Product repository port.
     * @param userRepository User repository port.
     * @param saleValidator Sale validator.
     * @param saleMapper Sale mapper.
     * @param authenticatedUserProvider Authenticated user provider.
     * @param companyRepository Company repository port.
     *@param companyContextProvider Company context provider.
     */
    public SaleService(
            SaleRepository saleRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            SaleValidator saleValidator,
            SaleMapper saleMapper,
            AuthenticatedUserProvider authenticatedUserProvider,
            CompanyContextProvider companyContextProvider,
            CompanyOidValidator companyOidValidator
    ) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.saleValidator = saleValidator;
        this.saleMapper = saleMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.companyContextProvider = companyContextProvider;
        this.companyOidValidator = companyOidValidator;
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

        CompanyOid companyOid = resolveCompanyOid(request.getCompanyOid());
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
        CompanyOid companyOid = resolveReadCompanyOid(requestedCompanyOid);
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
        CompanyOid companyOid = resolveReadCompanyOid(requestedCompanyOid);
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

    /**
     * Resolves the owning company for a write.
     *
     * <p>The company of the session always wins, so a caller can never move a record
     * out of the company its own token points at. Only when the token carries no
     * company, which is the case for the root user and for any session without a
     * company, the optional value supplied in the request is used. The identifier is
     * left {@code null} when neither is available, instead of being rejected.</p>
     *
     * <p>The value supplied in the request is validated against the stored companies
     * before it is used, so a record can never be attached to a company that does
     * not exist. The value is only validated when it is actually the one applied,
     * which is when the session carries no company.</p>
     *
     * @param requestedCompanyOid Optional company identifier supplied in the request.
     * @return Company identifier, or {@code null} when neither source provides one.
     * @throws ResourceNotFoundException when the requested company does not exist.
     */
    private CompanyOid resolveCompanyOid(String requestedCompanyOid) {
        Optional<CompanyOid> sessionCompany = companyContextProvider.currentCompanyOid();
        if (sessionCompany.isPresent()) {
            return sessionCompany.get();
        }
        if (requestedCompanyOid != null && !requestedCompanyOid.isBlank()) {
            return CompanyOid.of(companyOidValidator.validate(requestedCompanyOid));
        }
        return null;
    }

    /**
     * Resolves the owning company read by a query.
     *
     * <p>The company of the session always wins. Only when the token carries
     * none, which every caller without a claim faces, the company identifier
     * supplied in the request is used. Unlike the write path, where a missing
     * company is left {@code null}, a read has no separate bucket to fall back
     * to, so a missing, blank or invalid value is rejected.</p>
     *
     * @param requestedCompanyOid Company identifier supplied in the request.
     * @return Company to scope the query to.
     * @throws ResourceNotFoundException when no company can be resolved.
     */
    private CompanyOid resolveReadCompanyOid(String requestedCompanyOid) {
        Optional<CompanyOid> sessionCompany = companyContextProvider.currentCompanyOid();
        if (sessionCompany.isPresent()) {
            return sessionCompany.get();
        }
        if (requestedCompanyOid != null && !requestedCompanyOid.isBlank()) {
            return CompanyOid.of(companyOidValidator.validate(requestedCompanyOid));
        }
        throw new ResourceNotFoundException(AuthConstants.COMPANY_CONTEXT_REQUIRED);
    }
}
