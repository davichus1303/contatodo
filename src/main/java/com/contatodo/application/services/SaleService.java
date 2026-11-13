package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateSaleRequest;
import com.contatodo.application.dto.response.SaleResponse;
import com.contatodo.application.mapper.SaleMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.validators.SaleValidator;
import com.contatodo.application.validators.UnitOfMeasureValidator;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.Sale;
import com.contatodo.domain.entities.UnitOfMeasure;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.model.Money;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.domain.repositories.SaleRepository;
import com.contatodo.domain.repositories.UnitOfMeasureRepository;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.constants.SaleConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import com.contatodo.shared.exceptions.ResourceNotFoundException;
import com.contatodo.shared.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Use case service containing sale business logic.
 */
@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SaleValidator saleValidator;
    private final UnitOfMeasureValidator unitOfMeasureValidator;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final PackConversionService packConversionService;
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
     * @param unitOfMeasureValidator Unit of measure validator.
     * @param unitOfMeasureRepository Unit of measure repository used to join names on reads.
     * @param packConversionService Shared pack to base unit converter.
     * @param saleMapper Sale mapper.
     * @param authenticatedUserProvider Authenticated user provider.
     * @param companyService Company service used to resolve the owning company.
     */
    public SaleService(
            SaleRepository saleRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            SaleValidator saleValidator,
            UnitOfMeasureValidator unitOfMeasureValidator,
            UnitOfMeasureRepository unitOfMeasureRepository,
            PackConversionService packConversionService,
            SaleMapper saleMapper,
            AuthenticatedUserProvider authenticatedUserProvider,
            CompanyService companyService
    ) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.saleValidator = saleValidator;
        this.unitOfMeasureValidator = unitOfMeasureValidator;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.packConversionService = packConversionService;
        this.saleMapper = saleMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.companyService = companyService;
    }

    /**
     * Creates a new sale.
     *
     * <p>The sale belongs to the company of the session; only a session without
     * a company falls back to the identifier supplied in the request. The
     * product is then resolved inside that same company, so a sale can never
     * decrement the stock of a product owned by another company nor mix a
     * product of one company with a sale recorded under another. When neither
     * source provides a company the sale is rejected, keeping the sales page
     * and this write scoped identically.</p>
     *
     * <p>When the sale is registered in a pack unit, the stored quantity is the
     * quantity multiplied by the pack size, matching the base units removed
     * from stock.</p>
     *
     * @param request Create sale request.
     * @return Created sale response.
     * @throws ResourceNotFoundException when no company can be resolved, the product is missing from it or the user does not exist.
     */
    public SaleResponse createSale(CreateSaleRequest request) {
        saleValidator.validateCreateRequest(request);

        String userOid = authenticatedUserProvider.getCurrentUserOid();
        User user = userRepository.findById(userOid)
                .orElseThrow(() -> new ResourceNotFoundException(SaleConstants.USER_NOT_FOUND));

        CompanyOid companyOid = companyService.resolveRequiredCompanyOid(request.getCompanyOid());

        Product product = productRepository.findByIdAndCompany(companyOid, request.getProductOid())
                .orElseThrow(() -> new ResourceNotFoundException(SaleConstants.PRODUCT_NOT_FOUND));

        UnitOfMeasure unitOfMeasure = unitOfMeasureValidator.requireActive(companyOid, request.getUnitOfMeasureOid());
        if (request.getIsPack() != null && !Objects.equals(request.getIsPack(), unitOfMeasure.isPack())) {
            throw new InvalidRequestException(
                    ResponseConstants.VALIDATION_ERROR_MESSAGE,
                    List.of(SaleConstants.SALE_IS_PACK_MISMATCH)
            );
        }

        Integer packSize = request.getPackSize() != null ? request.getPackSize() : product.getPackSize();
        int baseQuantity = packConversionService.toBaseUnits(
                request.getQuantity(),
                unitOfMeasure.isPack(),
                packSize,
                SaleConstants.SALE_PACK_SIZE_REQUIRED
        );

        String productName = product.getName();
        Product updatedProduct = product.decreaseStock(baseQuantity);

        Money unitRealCost = Money.of(product.getUnitRealCost() != null ? product.getUnitRealCost() : product.getRealCost());
        Money unitPublicCost = Money.of(product.getUnitPublicCost() != null ? product.getUnitPublicCost() : product.getRealCost());

        Long saleNumber = generateDailySaleNumber(companyOid);

        Sale sale = Sale.place(
                saleNumber,
                request.getProductOid(),
                productName,
                user.getId(),
                baseQuantity,
                request.getUnitOfMeasureOid(),
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
        return saleMapper.toResponseListWithUnitOfMeasureNames(sales, loadUnitsOfMeasure(companyOid, sales));
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
        return saleMapper.toResponseListWithUnitOfMeasureNames(sales, loadUnitsOfMeasure(companyOid, sales));
    }

    /**
     * Loads the active units of measure referenced by the given sales.
     *
     * <p>Only the units of the resolved company are considered, so the join
     * behaves like an inner join: a sale whose unit is missing, inactive or
     * owned by another company keeps a {@code null} name.</p>
     *
     * @param companyOid Owning company identifier.
     * @param sales Sales whose unit identifiers must be resolved.
     * @return Referenced units of measure keyed by identifier.
     */
    private Map<String, UnitOfMeasure> loadUnitsOfMeasure(CompanyOid companyOid, List<Sale> sales) {
        Set<String> referencedOids = sales.stream()
                .map(Sale::getUnitOfMeasureOid)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (referencedOids.isEmpty()) {
            return Map.of();
        }
        return unitOfMeasureRepository.findActiveByCompany(companyOid).stream()
                .filter(unit -> referencedOids.contains(unit.getId()))
                .collect(Collectors.toMap(UnitOfMeasure::getId, Function.identity(), (first, second) -> first));
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
