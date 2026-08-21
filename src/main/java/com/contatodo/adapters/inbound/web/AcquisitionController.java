package com.contatodo.adapters.inbound.web;

import com.contatodo.application.dto.request.CreateAcquisitionRequest;
import com.contatodo.application.dto.response.AcquisitionResponse;
import com.contatodo.application.services.AcquisitionService;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.response.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for acquisition endpoints.
 */
@RestController
@RequestMapping("/acquisitions")
public class AcquisitionController {

    private final AcquisitionService acquisitionService;

    /**
     * Creates an acquisition controller.
     *
     * @param acquisitionService Acquisition service.
     */
    public AcquisitionController(AcquisitionService acquisitionService) {
        this.acquisitionService = acquisitionService;
    }

    /**
     * Registers a new acquisition.
     *
     * @param request Create acquisition request.
     * @return Created acquisition response.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AcquisitionResponse>> registerAcquisition(
      @RequestBody CreateAcquisitionRequest request) {
        AcquisitionResponse acquisition = acquisitionService.registerAcquisition(request);
        return ResponseEntity.ok(ApiResponse.success(ResponseConstants.SUCCESS_MESSAGE, acquisition));
    }

    /**
     * Retrieves acquisitions for the authenticated user.
     * By default, returns today's acquisitions.
     * Supports optional date range filtering.
     *
     * @param startDate Optional start date.
     * @param endDate Optional end date.
     * @return List of acquisition responses.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AcquisitionResponse>>> getAcquisitions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<AcquisitionResponse> acquisitions = acquisitionService.getAcquisitions(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(ResponseConstants.SUCCESS_MESSAGE, acquisitions));
    }
}
