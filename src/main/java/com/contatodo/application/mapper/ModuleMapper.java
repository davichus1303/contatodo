package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateModuleRequest;
import com.contatodo.application.dto.response.ModuleResponse;
import com.contatodo.domain.entities.Module;
import com.contatodo.domain.entities.ModulePermission;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper for module entities and DTOs.
 */
@Component
public class ModuleMapper {

    /**
     * Maps a create request to a domain entity.
     *
     * @param request Create module request.
     * @param userOid Authenticated user identifier.
     * @return Module entity.
     */
    public Module toEntity(CreateModuleRequest request, String userOid) {
        Module module = new Module();
        module.setName(request.getName());
        module.setLink(request.getLink());
        module.setIsActive(true);
        module.setIsDeleted(false);
        
        // Create permission for the authenticated user with full access
        ModulePermission permission = new ModulePermission(userOid, true, true, true);
        module.setPermissions(List.of(permission));
        
        module.setCreatedDate(LocalDateTime.now());
        module.setUpdatedDate(LocalDateTime.now());
        return module;
    }

    /**
     * Maps a module entity to a response DTO.
     *
     * @param module Module entity.
     * @return Module response.
     */
    public ModuleResponse toResponse(Module module) {
        ModuleResponse response = new ModuleResponse();
        response.setId(module.getId());
        response.setName(module.getName());
        response.setLink(module.getLink());
        return response;
    }

    /**
     * Maps a list of modules to response DTOs.
     *
     * @param modules Module entities.
     * @return Module responses.
     */
    public List<ModuleResponse> toResponseList(List<Module> modules) {
        return modules.stream().map(this::toResponse).toList();
    }
}
