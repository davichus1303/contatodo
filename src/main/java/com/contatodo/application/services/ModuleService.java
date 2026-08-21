package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateModuleRequest;
import com.contatodo.application.dto.response.ModuleResponse;
import com.contatodo.application.mapper.ModuleMapper;
import com.contatodo.domain.entities.Module;
import com.contatodo.domain.repositories.ModuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service containing module business logic.
 */
@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final ModuleMapper moduleMapper;

    /**
     * Creates a module service.
     *
     * @param moduleRepository Module repository port.
     * @param moduleMapper Module mapper.
     */
    public ModuleService(
            ModuleRepository moduleRepository,
            ModuleMapper moduleMapper
    ) {
        this.moduleRepository = moduleRepository;
        this.moduleMapper = moduleMapper;
    }

    /**
     * Creates a new module.
     *
     * @param request Create module request.
     * @return Created module response.
     */
    public ModuleResponse createModule(CreateModuleRequest request) {
        Module savedModule = moduleRepository.save(moduleMapper.toEntity(request));
        return moduleMapper.toResponse(savedModule);
    }

    /**
     * Retrieves all active modules.
     *
     * @return List of module responses.
     */
    public List<ModuleResponse> getModules() {
        List<Module> modules = moduleRepository.findAllActive();
        return moduleMapper.toResponseList(modules);
    }
}
