package com.contatodo.infrastructure.controller;

import com.contatodo.application.dto.request.CreateSaleRequest;
import com.contatodo.application.dto.response.SaleResponse;
import com.contatodo.application.services.SaleService;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.constants.SaleConstants;
import com.contatodo.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for sale endpoints.
 */
@RestController
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;

    /**
     * Creates a sale controller.
     *
     * @param saleService Sale service.
     */
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    /**
     * Creates a new sale.
     *
     * @param request Create sale request.
     * @return Created sale response.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SaleResponse>> createSale(@RequestBody CreateSaleRequest request) {
        SaleResponse sale = saleService.createSale(request);
        return ResponseEntity.ok(ApiResponse.success(SaleConstants.SALE_CREATED, sale));
    }

    /**
     * Retrieves today's sales for the authenticated user.
     *
     * @return List of sales.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SaleResponse>>> getTodaySales() {
        List<SaleResponse> sales = saleService.getTodaySales();
        return ResponseEntity.ok(ApiResponse.success(ResponseConstants.SUCCESS_MESSAGE, sales));
    }

    /**
     * Retrieves sales for the authenticated user by date.
     *
     * @param startDate Start date.
     * @param endDate End date.
     * @return List of sales.
     */
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<SaleResponse>>> getSalesByDateRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        List<SaleResponse> sales = saleService.getSalesByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(ResponseConstants.SUCCESS_MESSAGE, sales));
    }
}
