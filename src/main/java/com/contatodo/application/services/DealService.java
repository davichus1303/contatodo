package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateDealRequest;
import com.contatodo.application.dto.request.UpdateDealRequest;
import com.contatodo.application.dto.response.DealResponse;
import com.contatodo.application.mapper.DealMapper;
import com.contatodo.domain.entities.Deal;
import com.contatodo.domain.model.CompanyOid;
import com.contatodo.domain.repositories.DealRepository;
import com.contatodo.shared.constants.DealConstants;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service containing deal business logic.
 */
@Service
public class DealService {

    private final DealRepository dealRepository;
    private final DealMapper dealMapper;
    private final CompanyService companyService;
    private final CacheManager cacheManager;

    /**
     * Creates a deal service.
     *
     * @param dealRepository Deal repository port.
     * @param dealMapper Deal mapper.
     * @param companyService Company service used to resolve the owning company.
     * @param cacheManager Cache manager used to read and evict deal caches.
     */
    public DealService(
            DealRepository dealRepository,
            DealMapper dealMapper,
            CompanyService companyService,
            CacheManager cacheManager
    ) {
        this.dealRepository = dealRepository;
        this.dealMapper = dealMapper;
        this.companyService = companyService;
        this.cacheManager = cacheManager;
    }

    /**
     * Retrieves all non-deleted deals belonging to the specified company.
     *
     * @param requestedCompanyOid Company identifier supplied in the request.
     * @return List of deal responses.
     */
    @SuppressWarnings("unchecked")
    public List<DealResponse> getDealsByCompany(String requestedCompanyOid) {
        validateRequiredString(requestedCompanyOid, DealConstants.COMPANY_OID_REQUIRED);
        CompanyOid companyOid = companyService.resolveReadCompanyOid(requestedCompanyOid);

        Cache cache = cacheManager.getCache(DealConstants.CACHE_DEALS_BY_COMPANY);
        String key = companyKey(companyOid);
        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null && wrapper.get() instanceof List<?>) {
                return (List<DealResponse>) wrapper.get();
            }
        }

        List<DealResponse> response = dealMapper.toResponseList(
                dealRepository.findByCompanyAndNotDeleted(companyOid)
        );
        if (cache != null) {
            cache.put(key, response);
        }
        return response;
    }

    /**
     * Retrieves all active and non-deleted deals belonging to the specified company.
     *
     * @param requestedCompanyOid Company identifier supplied in the request.
     * @return List of active, non-deleted deal responses.
     */
    @SuppressWarnings("unchecked")
    public List<DealResponse> getActiveNotDeletedDealsByCompany(String requestedCompanyOid) {
        validateRequiredString(requestedCompanyOid, DealConstants.COMPANY_OID_REQUIRED);
        CompanyOid companyOid = companyService.resolveReadCompanyOid(requestedCompanyOid);

        Cache cache = cacheManager.getCache(DealConstants.CACHE_ACTIVE_NOT_DELETED_DEALS_BY_COMPANY);
        String key = companyKey(companyOid);
        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null && wrapper.get() instanceof List<?>) {
                return (List<DealResponse>) wrapper.get();
            }
        }

        List<DealResponse> response = dealMapper.toResponseList(
                dealRepository.findActiveNotDeletedByCompany(companyOid)
        );
        if (cache != null) {
            cache.put(key, response);
        }
        return response;
    }

    /**
     * Retrieves the active, non-deleted deal for the given company and product.
     *
     * @param requestedCompanyOid Company identifier supplied in the request.
     * @param productOid Product identifier supplied in the request.
     * @return Deal response or an empty map when not found.
     */
    public Object getOfferByProduct(String requestedCompanyOid, String productOid) {
        validateRequiredString(requestedCompanyOid, DealConstants.COMPANY_OID_REQUIRED);
        validateRequiredString(productOid, DealConstants.PRODUCT_OID_REQUIRED);
        CompanyOid companyOid = companyService.resolveReadCompanyOid(requestedCompanyOid);

        Cache cache = cacheManager.getCache(DealConstants.CACHE_DEAL_OFFER_BY_PRODUCT);
        String key = offerKey(companyOid, productOid);
        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null) {
                return wrapper.get();
            }
        }

        Object response = dealRepository.findActiveByCompanyAndProduct(companyOid, productOid)
                .<Object>map(dealMapper::toResponse)
                .orElseGet(Map::of);
        if (cache != null) {
            cache.put(key, response);
        }
        return response;
    }

    /**
     * Creates a new deal.
     *
     * @param request Create deal request.
     * @return Created deal response.
     */
    public DealResponse createDeal(CreateDealRequest request) {
        validateCreateRequest(request);
        CompanyOid companyOid = companyService.resolveRequiredCompanyOid(request.getCompanyOid());

        if (existsDealForCompanyAndProduct(companyOid, request.getProductOid())) {
            throw new InvalidRequestException(
                    ResponseConstants.VALIDATION_ERROR_MESSAGE,
                    List.of(DealConstants.ALREADY_EXISTS_ERROR)
            );
        }

        LocalDateTime now = LocalDateTime.now();
        CreateDealRequest normalizedRequest = normalizeCreateRequest(request, companyOid.value());
        Deal savedDeal = dealRepository.save(dealMapper.toEntity(normalizedRequest, now));
        evictCaches(companyOid, savedDeal.getProductOid());
        return dealMapper.toResponse(savedDeal);
    }

    /**
     * Checks whether a non-deleted deal already exists for the given company and product.
     *
     * @param companyOid Owning company.
     * @param productOid Product identifier.
     * @return {@code true} when a non-deleted deal exists.
     */
    public boolean existsDealForCompanyAndProduct(CompanyOid companyOid, String productOid) {
        return dealRepository.findNotDeletedByCompanyAndProduct(companyOid, productOid).isPresent();
    }

    /**
     * Updates an existing deal.
     *
     * @param oid Deal identifier.
     * @param request Update request.
     * @return Updated deal response.
     */
    public DealResponse updateDeal(String oid, UpdateDealRequest request) {
        validateRequiredString(oid, DealConstants.OID_REQUIRED);
        if (request == null) {
            request = new UpdateDealRequest();
        }
        validateUpdateRequest(request);

        Deal existingDeal = dealRepository.findById(oid)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseConstants.VALIDATION_ERROR_MESSAGE,
                        List.of(DealConstants.NOT_FOUND_ERROR)
                ));

        LocalDateTime now = LocalDateTime.now();
        Deal updatedDeal = dealMapper.applyUpdate(existingDeal, request, now);
        Deal savedDeal = dealRepository.save(updatedDeal);
        evictCaches(CompanyOid.of(savedDeal.getCompanyOid()), savedDeal.getProductOid());
        return dealMapper.toResponse(savedDeal);
    }

    /**
     * Performs a soft deletion of a deal.
     *
     * @param oid Deal identifier.
     */
    public void deleteDeal(String oid) {
        validateRequiredString(oid, DealConstants.OID_REQUIRED);

        Deal existingDeal = dealRepository.findById(oid)
                .orElseThrow(() -> new InvalidRequestException(
                        ResponseConstants.VALIDATION_ERROR_MESSAGE,
                        List.of(DealConstants.NOT_FOUND_ERROR)
                ));

        Deal deletedDeal = existingDeal.markDeleted(LocalDateTime.now());
        Deal savedDeal = dealRepository.save(deletedDeal);
        evictCaches(CompanyOid.of(savedDeal.getCompanyOid()), savedDeal.getProductOid());
    }

    /**
     * Validates the payload required to create a deal.
     *
     * @param request Create deal request.
     * @throws InvalidRequestException when required data is missing or invalid.
     */
    private void validateCreateRequest(CreateDealRequest request) {
        if (request == null) {
            throw new InvalidRequestException(
                    ResponseConstants.VALIDATION_ERROR_MESSAGE,
                    List.of(DealConstants.COMPANY_OID_REQUIRED,
                            DealConstants.PRODUCT_OID_REQUIRED,
                            DealConstants.UNIT_OF_MEASURE_OID_REQUIRED,
                            DealConstants.PRICE_REQUIRED,
                            DealConstants.QUANTITY_REQUIRED)
            );
        }

        validateRequiredString(request.getCompanyOid(), DealConstants.COMPANY_OID_REQUIRED);
        validateRequiredString(request.getProductOid(), DealConstants.PRODUCT_OID_REQUIRED);
        validateRequiredString(request.getUnitOfMeasureOid(), DealConstants.UNIT_OF_MEASURE_OID_REQUIRED);

        if (request.getPrice() == null) {
            throw new InvalidRequestException(
                    ResponseConstants.VALIDATION_ERROR_MESSAGE,
                    List.of(DealConstants.PRICE_REQUIRED)
            );
        }
        if (request.getQuantity() == null) {
            throw new InvalidRequestException(
                    ResponseConstants.VALIDATION_ERROR_MESSAGE,
                    List.of(DealConstants.QUANTITY_REQUIRED)
            );
        }
        if (request.getPrice().isNaN() || request.getPrice().isInfinite()) {
            throw new InvalidRequestException(
                    ResponseConstants.VALIDATION_ERROR_MESSAGE,
                    List.of(DealConstants.PRICE_INVALID)
            );
        }
        if (request.getQuantity().isNaN() || request.getQuantity().isInfinite()) {
            throw new InvalidRequestException(
                    ResponseConstants.VALIDATION_ERROR_MESSAGE,
                    List.of(DealConstants.QUANTITY_INVALID)
            );
        }
    }

    /**
     * Validates the numeric fields provided in an update request.
     *
     * @param request Update deal request.
     * @throws InvalidRequestException when a provided numeric value is invalid.
     */
    private void validateUpdateRequest(UpdateDealRequest request) {
        if (request.getPrice() != null && (request.getPrice().isNaN() || request.getPrice().isInfinite())) {
            throw new InvalidRequestException(
                    ResponseConstants.VALIDATION_ERROR_MESSAGE,
                    List.of(DealConstants.PRICE_INVALID)
            );
        }
        if (request.getQuantity() != null && (request.getQuantity().isNaN() || request.getQuantity().isInfinite())) {
            throw new InvalidRequestException(
                    ResponseConstants.VALIDATION_ERROR_MESSAGE,
                    List.of(DealConstants.QUANTITY_INVALID)
            );
        }
    }

    /**
     * Builds a normalized create request using the resolved company identifier.
     *
     * @param request Original create deal request.
     * @param resolvedCompanyOid Resolved owning company identifier.
     * @return Normalized create deal request.
     */
    private CreateDealRequest normalizeCreateRequest(CreateDealRequest request, String resolvedCompanyOid) {
        CreateDealRequest normalized = new CreateDealRequest();
        normalized.setCompanyOid(resolvedCompanyOid);
        normalized.setProductOid(request.getProductOid());
        normalized.setUnitOfMeasureOid(request.getUnitOfMeasureOid());
        normalized.setPrice(request.getPrice());
        normalized.setQuantity(request.getQuantity());
        return normalized;
    }

    /**
     * Validates that a required string value is present.
     *
     * @param value Value to validate.
     * @param errorMessage Validation error message.
     * @throws InvalidRequestException when the value is null or blank.
     */
    private void validateRequiredString(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException(
                    ResponseConstants.VALIDATION_ERROR_MESSAGE,
                    List.of(errorMessage)
            );
        }
    }

    /**
     * Evicts deal caches affected by a change in the given company and product.
     *
     * @param companyOid Owning company.
     * @param productOid Product identifier.
     */
    private void evictCaches(CompanyOid companyOid, String productOid) {
        Cache dealsCache = cacheManager.getCache(DealConstants.CACHE_DEALS_BY_COMPANY);
        if (dealsCache != null) {
            dealsCache.evict(companyKey(companyOid));
        }
        Cache activeNotDeletedDealsCache = cacheManager.getCache(DealConstants.CACHE_ACTIVE_NOT_DELETED_DEALS_BY_COMPANY);
        if (activeNotDeletedDealsCache != null) {
            activeNotDeletedDealsCache.evict(companyKey(companyOid));
        }
        Cache offerCache = cacheManager.getCache(DealConstants.CACHE_DEAL_OFFER_BY_PRODUCT);
        if (offerCache != null) {
            offerCache.evict(offerKey(companyOid, productOid));
        }
    }

    /**
     * Builds the cache key for company-scoped deal entries.
     *
     * @param companyOid Owning company.
     * @return Cache key for the company.
     */
    private String companyKey(CompanyOid companyOid) {
        return companyOid != null ? companyOid.value() : "null";
    }

    /**
     * Builds the cache key for an offer lookup by company and product.
     *
     * @param companyOid Owning company.
     * @param productOid Product identifier.
     * @return Cache key for the offer lookup.
     */
    private String offerKey(CompanyOid companyOid, String productOid) {
        return companyKey(companyOid) + "::" + productOid;
    }
}
