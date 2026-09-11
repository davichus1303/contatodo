package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateAcquisitionRequest;
import com.contatodo.application.dto.request.CreateExpenseRequest;
import com.contatodo.application.dto.response.AcquisitionResponse;
import com.contatodo.application.mapper.AcquisitionMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.validators.AcquisitionValidator;
import com.contatodo.domain.entities.Acquisition;
import com.contatodo.domain.entities.AcquisitionType;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.repositories.AcquisitionRepository;
import com.contatodo.domain.repositories.AcquisitionTypeRepository;
import com.contatodo.domain.repositories.ProductRepository;
import com.contatodo.shared.constants.AcquisitionTypeConstants;
import com.contatodo.shared.constants.ExpenseConstants;
import com.contatodo.shared.exceptions.AcquisitionTypeNotFoundException;
import com.contatodo.shared.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Use case service orchestrating acquisition registration and queries.
 *
 * <p>An acquisition either affects inventory (delegated to
 * {@link ProductInventoryHandler}) or is recorded as an expense. This class
 * coordinates the flow without owning inventory rules itself.</p>
 */
@Service
public class AcquisitionService {

    private final AcquisitionRepository acquisitionRepository;
    private final ProductRepository productRepository;
    private final AcquisitionTypeRepository acquisitionTypeRepository;
    private final AcquisitionValidator acquisitionValidator;
    private final AcquisitionMapper acquisitionMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ExpenseService expenseService;
    private final ProductInventoryHandler productInventoryHandler;

    /**
     * Creates an acquisition service.
     *
     * @param acquisitionRepository Acquisition repository port.
     * @param productRepository Product repository port.
     * @param acquisitionTypeRepository Acquisition type repository port.
     * @param acquisitionValidator Acquisition validator.
     * @param acquisitionMapper Acquisition mapper.
     * @param authenticatedUserProvider Authenticated user provider.
     * @param expenseService Expense use case service.
     * @param productInventoryHandler Inventory side effects handler.
     */
    public AcquisitionService(
            AcquisitionRepository acquisitionRepository,
            ProductRepository productRepository,
            AcquisitionTypeRepository acquisitionTypeRepository,
            AcquisitionValidator acquisitionValidator,
            AcquisitionMapper acquisitionMapper,
            AuthenticatedUserProvider authenticatedUserProvider,
            ExpenseService expenseService,
            ProductInventoryHandler productInventoryHandler
    ) {
        this.acquisitionRepository = acquisitionRepository;
        this.productRepository = productRepository;
        this.acquisitionTypeRepository = acquisitionTypeRepository;
        this.acquisitionValidator = acquisitionValidator;
        this.acquisitionMapper = acquisitionMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.expenseService = expenseService;
        this.productInventoryHandler = productInventoryHandler;
    }

    /**
     * Registers a new acquisition dispatching to the inventory or expense flow.
     *
     * @param request Create acquisition request.
     * @return Created acquisition response.
     */
    public AcquisitionResponse registerAcquisition(CreateAcquisitionRequest request) {
        String userOid = authenticatedUserProvider.getCurrentUserOid();

        AcquisitionType acquisitionType = findAcquisitionType(request.getAcquisitionTypeOid());

        if (acquisitionType.affectsInventory()) {
            acquisitionValidator.validateCreateRequest(request);
            return registerInventoryAffectingAcquisition(request, userOid, acquisitionType);
        }

        acquisitionValidator.validateNonInventoryAffectingRequest(request);
        return registerNonInventoryAffectingAcquisition(request, userOid, acquisitionType);
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
        String userOid = authenticatedUserProvider.getCurrentUserOid();

        List<Acquisition> acquisitions = acquisitionRepository.findByUserOidAndAcquisitionDateBetween(
                userOid,
                startDate != null ? startDate : DateUtils.startOfDay(LocalDate.now()),
                endDate != null ? endDate : DateUtils.endOfDay(LocalDate.now())
        );

        Map<String, String> productNames = enrichNames(
                acquisitions, Acquisition::getProductOid, productRepository::findById, Product::getName);
        Map<String, String> acquisitionTypes = enrichNames(
                acquisitions, Acquisition::getAcquisitionTypeOid, acquisitionTypeRepository::findById,
                AcquisitionType::getName);

        return acquisitionMapper.toResponseList(acquisitions, productNames, acquisitionTypes);
    }

    /**
     * Registers an acquisition that affects inventory.
     *
     * <p>The product creation or replenishment plus its cost history are
     * delegated to {@link ProductInventoryHandler}.</p>
     *
     * @param request Create acquisition request.
     * @param userOid User OID.
     * @param acquisitionType Acquisition type entity.
     * @return Created acquisition response.
     */
    private AcquisitionResponse registerInventoryAffectingAcquisition(
            CreateAcquisitionRequest request,
            String userOid,
            AcquisitionType acquisitionType
    ) {
        ProductInventoryHandler.InventoryOutcome outcome =
                productInventoryHandler.applyToInventory(request, userOid);

        Acquisition acquisition = acquisitionMapper.toEntity(
                request, outcome.productOid(), userOid, outcome.averageUnitRealCost());
        Acquisition savedAcquisition = acquisitionRepository.save(acquisition);

        productInventoryHandler.recordCostHistory(
                outcome.productOid(), request, savedAcquisition, outcome.averageUnitRealCost());

        return acquisitionMapper.toResponse(savedAcquisition, outcome.productName(), acquisitionType.getName());
    }

    /**
     * Registers an acquisition that does not affect inventory by creating an
     * expense for it.
     *
     * @param request Create acquisition request.
     * @param userOid User OID.
     * @param acquisitionType Acquisition type entity.
     * @return Created acquisition response.
     */
    private AcquisitionResponse registerNonInventoryAffectingAcquisition(
            CreateAcquisitionRequest request,
            String userOid,
            AcquisitionType acquisitionType
    ) {
        Acquisition acquisition = acquisitionMapper.toEntity(request, null, userOid, 0.0);
        Acquisition savedAcquisition = acquisitionRepository.save(acquisition);

        CreateExpenseRequest expenseRequest = new CreateExpenseRequest();
        expenseRequest.setAcquisitionOid(savedAcquisition.getId());
        expenseRequest.setAcquisitionTypeOid(acquisitionType.getId());
        expenseRequest.setName(request.getProductName());
        expenseRequest.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);
        expenseRequest.setAmount(request.getRealCost());
        expenseRequest.setCurrency(ExpenseConstants.DEFAULT_CURRENCY);
        expenseRequest.setExpenseDate(formatIso(savedAcquisition.getAcquisitionDate()));

        expenseService.createExpense(expenseRequest);

        return acquisitionMapper.toResponse(savedAcquisition, request.getProductName(), acquisitionType.getName());
    }

    /**
     * Finds an acquisition type by identifier.
     *
     * @param acquisitionTypeOid Acquisition type identifier.
     * @return Acquisition type entity.
     * @throws AcquisitionTypeNotFoundException if the type does not exist.
     */
    private AcquisitionType findAcquisitionType(String acquisitionTypeOid) {
        return acquisitionTypeRepository.findById(acquisitionTypeOid)
                .orElseThrow(() -> new AcquisitionTypeNotFoundException(AcquisitionTypeConstants.NOT_FOUND_ERROR));
    }

    /**
     * Resolves a name map for a related entity, looking each OID up at most once.
     *
     * @param acquisitions List of acquisitions.
     * @param oidExtractor Extracts the related OID from an acquisition.
     * @param finder Looks up the related entity by OID.
     * @param nameExtractor Extracts the related entity name.
     * @param <T> Related entity type.
     * @return Map of related OIDs to names.
     */
    private <T> Map<String, String> enrichNames(
            List<Acquisition> acquisitions,
            Function<Acquisition, String> oidExtractor,
            Function<String, Optional<T>> finder,
            Function<T, String> nameExtractor
    ) {
        Map<String, String> names = new HashMap<>();
        for (Acquisition acquisition : acquisitions) {
            String oid = oidExtractor.apply(acquisition);
            if (oid != null && !names.containsKey(oid)) {
                finder.apply(oid).ifPresent(entity -> names.put(oid, nameExtractor.apply(entity)));
            }
        }
        return names;
    }

    /**
     * Formats a timestamp using the ISO-8601 local date time pattern.
     *
     * @param dateTime Timestamp to format.
     * @return Formatted text, or the current timestamp when null.
     */
    private String formatIso(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return (dateTime != null ? dateTime : LocalDateTime.now()).format(formatter);
    }
}
