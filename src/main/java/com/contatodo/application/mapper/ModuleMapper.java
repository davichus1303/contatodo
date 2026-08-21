package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateModuleRequest;
import com.contatodo.application.dto.response.ModuleResponse;
import com.contatodo.domain.entities.Module;
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
     * @return Module entity.
     */
    public Module toEntity(CreateModuleRequest request) {
        LocalDateTime now = LocalDateTime.now();
        return Module.builder()
                .name(request.getName())
                .link(request.getLink())
                .isActive(true)
                .isDeleted(false)
                .createdDate(now)
                .updatedDate(now)
                .build();
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
