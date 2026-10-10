package com.contatodo.adapters.inbound.web;

import com.contatodo.application.dto.request.CreateUnitOfMeasureRequest;
import com.contatodo.application.dto.request.UpdateUnitOfMeasureRequest;
import com.contatodo.application.dto.response.UnitOfMeasureResponse;
import com.contatodo.application.services.UnitOfMeasureService;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.constants.UnitOfMeasureConstants;
import com.contatodo.shared.response.ApiResponse;
import com.contatodo.shared.response.WebResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * Creates a new unit of measure for the resolved company.
     *
     * @param request Create unit of measure request.
     * @return Created unit of measure.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UnitOfMeasureResponse>> createUnitOfMeasure(
            @RequestBody CreateUnitOfMeasureRequest request) {
        UnitOfMeasureResponse unitOfMeasure = unitOfMeasureService.createUnitOfMeasure(request);
        return WebResponses.ok(UnitOfMeasureConstants.CREATED_SUCCESS, unitOfMeasure);
    }

    /**
     * Updates an existing unit of measure.
     *
     * @param unitOfMeasuresOid Unit of measure identifier.
     * @param companyOid Optional company identifier supplied in the request.
     * @param request Update unit of measure request.
     * @return Updated unit of measure.
     */
    @PutMapping("/{unitOfMeasuresOid}")
    public ResponseEntity<ApiResponse<UnitOfMeasureResponse>> updateUnitOfMeasure(
            @PathVariable String unitOfMeasuresOid,
            @RequestParam(required = false) String companyOid,
            @RequestBody UpdateUnitOfMeasureRequest request) {
        UnitOfMeasureResponse unitOfMeasure =
                unitOfMeasureService.updateUnitOfMeasureControl(companyOid, request, unitOfMeasuresOid);
        return WebResponses.ok(UnitOfMeasureConstants.UPDATED_SUCCESS, unitOfMeasure);
    }

    /**
     * Logically deletes a unit of measure.
     *
     * @param unitOfMeasuresOid Unit of measure identifier.
     * @param companyOid Optional company identifier supplied in the request.
     * @return Response without the deleted unit data.
     */
    @DeleteMapping("/{unitOfMeasuresOid}")
    public ResponseEntity<ApiResponse<List<Object>>> deleteUnitOfMeasure(
            @PathVariable String unitOfMeasuresOid,
            @RequestParam(required = false) String companyOid) {
        unitOfMeasureService.deleteUnitOfMeasureControl(unitOfMeasuresOid, companyOid);
        return WebResponses.okNoData(ResponseConstants.DELETED_MESSAGE);
    }
}
