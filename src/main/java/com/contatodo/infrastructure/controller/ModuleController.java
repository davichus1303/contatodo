package com.contatodo.infrastructure.controller;

import com.contatodo.application.dto.request.CreateModuleRequest;
import com.contatodo.application.dto.response.ModuleResponse;
import com.contatodo.application.services.ModuleService;
import com.contatodo.shared.constants.ModuleConstants;
import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for module endpoints.
 */
@RestController
@RequestMapping("/modules")
public class ModuleController {

    private final ModuleService moduleService;

    /**
     * Creates a module controller.
     *
     * @param moduleService Module service.
     */
    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    /**
     * Creates a new module.
     *
     * @param request Create module request.
     * @return Created module response.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ModuleResponse>> createModule(@RequestBody CreateModuleRequest request) {
        ModuleResponse module = moduleService.createModule(request);
        return ResponseEntity.ok(ApiResponse.success(ModuleConstants.MODULE_CREATED, module));
    }

    /**
     * Retrieves modules accessible to the authenticated user.
     * @return List of modules.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getModulesForUser() {
        List<ModuleResponse> modules = moduleService.getModulesForUser();
        return ResponseEntity.ok(ApiResponse.success(ResponseConstants.SUCCESS_MESSAGE, modules));
    }
}
