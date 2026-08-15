package com.contatodo.infrastructure.controller;

import com.contatodo.application.dto.request.CreateRoleRequest;
import com.contatodo.application.dto.request.UpdateRoleRequest;
import com.contatodo.application.dto.response.RoleResponse;
import com.contatodo.application.services.RoleService;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for role endpoints.
 */
@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    /**
     * Creates a role controller.
     *
     * @param roleService Role service.
     */
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Creates a new role.
     * 
     * @param request Create role request.
     * @return Success response.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<List<Object>>> createRole(@Valid @RequestBody CreateRoleRequest request) {
        roleService.createRole(request);
        return ResponseEntity.ok(ApiResponse.successWithoutData(ResponseConstants.CREATED_MESSAGE));
    }

    /**
     * Retrieves all active roles.
     * @return List of roles.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getRoles() {
        List<RoleResponse> roles = roleService.getRoles();
        return ResponseEntity.ok(ApiResponse.success(ResponseConstants.SUCCESS_MESSAGE, roles));
    }

    /**
     * Updates an existing role.
     * 
     * @param id Role identifier.
     * @param request Update role request.
     * @return Success response.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<List<Object>>> updateRole(
            @PathVariable String id,
            @RequestBody UpdateRoleRequest request) {
        roleService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.successWithoutData(ResponseConstants.UPDATED_MESSAGE));
    }

    /**
     * Logically deletes a role.
     * 
     * @param id Role identifier.
     * @return Success response.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<List<Object>>> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.successWithoutData(ResponseConstants.DELETED_MESSAGE));
    }
}
