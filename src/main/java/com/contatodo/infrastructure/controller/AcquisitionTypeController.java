package com.contatodo.infrastructure.controller;

import com.contatodo.application.dto.request.CreateAcquisitionTypeRequest;
import com.contatodo.application.dto.request.UpdateAcquisitionTypeRequest;
import com.contatodo.application.dto.response.AcquisitionTypeResponse;
import com.contatodo.application.services.AcquisitionTypeService;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for acquisition type endpoints.
 */
@RestController
@RequestMapping("/acquisition-types")
public class AcquisitionTypeController {

    private final AcquisitionTypeService acquisitionTypeService;

    /**
     * Creates an acquisition type controller.
     *
     * @param acquisitionTypeService Acquisition type service.
     */
    public AcquisitionTypeController(AcquisitionTypeService acquisitionTypeService) {
        this.acquisitionTypeService = acquisitionTypeService;
    }

    /**
     * Creates a new acquisition type.
     *
     * @param request Create acquisition type request.
     * @return Created acquisition type response.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AcquisitionTypeResponse>> createAcquisitionType(
            @RequestBody CreateAcquisitionTypeRequest request
    ) {
        AcquisitionTypeResponse acquisitionType = acquisitionTypeService.createAcquisitionType(request);
        return ResponseEntity.ok(ApiResponse.success(ResponseConstants.SUCCESS_MESSAGE, acquisitionType));
    }

    /**
     * Retrieves all active and non-deleted acquisition types.
     *
     * @return List of acquisition types.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AcquisitionTypeResponse>>> getActiveAcquisitionTypes() {
        List<AcquisitionTypeResponse> acquisitionTypes = acquisitionTypeService.getActiveAcquisitionTypes();
        return ResponseEntity.ok(ApiResponse.success(ResponseConstants.SUCCESS_MESSAGE, acquisitionTypes));
    }

    /**
     * Updates an existing acquisition type.
     *
     * @param id Acquisition type ID.
     * @param request Update acquisition type request.
     * @return Updated acquisition type response.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AcquisitionTypeResponse>> updateAcquisitionType(
            @PathVariable String id,
            @RequestBody UpdateAcquisitionTypeRequest request
    ) {
        AcquisitionTypeResponse acquisitionType = acquisitionTypeService.updateAcquisitionType(id, request);
        return ResponseEntity.ok(ApiResponse.success(ResponseConstants.SUCCESS_MESSAGE, acquisitionType));
    }

    /**
     * Logically deletes an acquisition type.
     *
     * @param id Acquisition type ID.
     * @return Deleted acquisition type response.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<AcquisitionTypeResponse>> deleteAcquisitionType(
            @PathVariable String id
    ) {
        AcquisitionTypeResponse acquisitionType = acquisitionTypeService.deleteAcquisitionType(id);
        return ResponseEntity.ok(ApiResponse.success(ResponseConstants.SUCCESS_MESSAGE, acquisitionType));
    }

    /**
     * Retrieves all non-deleted acquisition types (both active and inactive) for administration.
     *
     * @return List of acquisition types ordered by name.
     */
    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<List<AcquisitionTypeResponse>>> getAllNotDeletedAcquisitionTypes() {
        List<AcquisitionTypeResponse> acquisitionTypes = acquisitionTypeService.getAllNotDeletedAcquisitionTypes();
        return ResponseEntity.ok(ApiResponse.success(ResponseConstants.SUCCESS_MESSAGE, acquisitionTypes));
    }
}
