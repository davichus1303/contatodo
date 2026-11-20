package com.contatodo.adapters.inbound.web;

import com.contatodo.application.dto.request.CreateDealRequest;
import com.contatodo.application.dto.request.UpdateDealRequest;
import com.contatodo.application.dto.response.DealResponse;
import com.contatodo.application.services.DealService;
import com.contatodo.shared.constants.DealConstants;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.response.ApiResponse;
import com.contatodo.shared.response.WebResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for deal endpoints.
 */
@RestController
@RequestMapping("/deals")
public class DealController {

    private final DealService dealService;

    /**
     * Creates a deal controller.
     *
     * @param dealService Deal service.
     */
    public DealController(DealService dealService) {
        this.dealService = dealService;
    }

    /**
     * Retrieves all deals belonging to a company.
     *
     * @param companyOid Company identifier.
     * @return List of deals.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DealResponse>>> getDeals(
            @RequestParam(required = false) String companyOid) {
        List<DealResponse> deals = dealService.getDealsByCompany(companyOid);
        return WebResponses.ok(ResponseConstants.SUCCESS_MESSAGE, deals);
    }

    /**
     * Retrieves all active and non-deleted deals belonging to a company.
     *
     * @param companyOid Company identifier.
     * @return List of active, non-deleted deals.
     */
    @GetMapping("/activeNotDeleted")
    public ResponseEntity<ApiResponse<List<DealResponse>>> getActiveNotDeletedDeals(
            @RequestParam(required = false) String companyOid) {
        List<DealResponse> deals = dealService.getActiveNotDeletedDealsByCompany(companyOid);
        return WebResponses.ok(ResponseConstants.SUCCESS_MESSAGE, deals);
    }

    /**
     * Retrieves the active deal for a specific product within a company.
     *
     * @param companyOid Company identifier.
     * @param productOid Product identifier.
     * @return Matching deal or an empty object.
     */
    @GetMapping("/offerByProduct")
    public ResponseEntity<ApiResponse<Object>> getOfferByProduct(
            @RequestParam(required = false) String companyOid,
            @RequestParam(required = false) String productOid) {
        Object deal = dealService.getOfferByProduct(companyOid, productOid);
        return WebResponses.ok(ResponseConstants.SUCCESS_MESSAGE, deal);
    }

    /**
     * Creates a new deal.
     *
     * @param request Create deal request.
     * @return Created deal.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DealResponse>> createDeal(@RequestBody CreateDealRequest request) {
        DealResponse deal = dealService.createDeal(request);
        return WebResponses.ok(DealConstants.CREATED_SUCCESS, deal);
    }

    /**
     * Updates an existing deal.
     *
     * @param oid Deal identifier.
     * @param request Update deal request.
     * @return Updated deal.
     */
    @PutMapping
    public ResponseEntity<ApiResponse<DealResponse>> updateDeal(
            @RequestParam(required = false) String oid,
            @RequestBody(required = false) UpdateDealRequest request) {
        DealResponse deal = dealService.updateDeal(oid, request);
        return WebResponses.ok(DealConstants.UPDATED_SUCCESS, deal);
    }

    /**
     * Performs a soft deletion of a deal.
     *
     * @param oid Deal identifier.
     * @return Success response without data.
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<List<Object>>> deleteDeal(@RequestParam(required = false) String oid) {
        dealService.deleteDeal(oid);
        return WebResponses.okNoData(ResponseConstants.DELETED_MESSAGE);
    }
}
