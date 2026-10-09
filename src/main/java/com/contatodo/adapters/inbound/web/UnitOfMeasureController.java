package com.contatodo.adapters.inbound.web;

import com.contatodo.application.dto.response.UnitOfMeasureResponse;
import com.contatodo.application.services.UnitOfMeasureService;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.response.ApiResponse;
import com.contatodo.shared.response.WebResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for unit of measure endpoints.
 */
@RestController
@RequestMapping("/units-of-measure")
public class UnitOfMeasureController {

    private final UnitOfMeasureService unitOfMeasureService;

    /**
     * Creates a unit of measure controller.
     *
     * @param unitOfMeasureService Unit of measure service.
     */
    public UnitOfMeasureController(UnitOfMeasureService unitOfMeasureService) {
        this.unitOfMeasureService = unitOfMeasureService;
    }

    /**
     * Retrieves the active, non-deleted units of measure of the resolved company.
     *
     * @param companyOid Optional company identifier supplied in the request.
     * @return List of units of measure.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UnitOfMeasureResponse>>> getActiveUnitsOfMeasure(
            @RequestParam(required = false) String companyOid) {
        List<UnitOfMeasureResponse> unitsOfMeasure = unitOfMeasureService.getActiveUnitsOfMeasure(companyOid);
        return WebResponses.ok(ResponseConstants.SUCCESS_MESSAGE, unitsOfMeasure);
    }
}
