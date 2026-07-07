package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateModuleRequest;
import com.contatodo.application.dto.response.ModuleResponse;
import com.contatodo.application.mapper.ModuleMapper;
import com.contatodo.domain.entities.Module;
import com.contatodo.domain.repositories.ModuleRepository;
import com.contatodo.shared.constants.ModuleConstants;
import com.contatodo.shared.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service containing module business logic.
 */
@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final ModuleMapper moduleMapper;
    private final UserService userService;

    /**
     * Creates a module service.
     *
     * @param moduleRepository Module repository port.
     * @param moduleMapper Module mapper.
     * @param userService User service.
     */
    public ModuleService(
            ModuleRepository moduleRepository,
            ModuleMapper moduleMapper,
            UserService userService
    ) {
        this.moduleRepository = moduleRepository;
        this.moduleMapper = moduleMapper;
        this.userService = userService;
    }

    /**
     * Creates a new module.
     *
     * @param request Create module request.
     * @return Created module response.
     */
    public ModuleResponse createModule(CreateModuleRequest request) {
        String userOid = SecurityUtils.getCurrentUserOid(userService);
        
        Module module = moduleMapper.toEntity(request, userOid);
        Module savedModule = moduleRepository.save(module);
        return moduleMapper.toResponse(savedModule);
    }

    /**
     * Retrieves modules accessible to the authenticated user.
     *
     * @return List of module responses.
     */
    public List<ModuleResponse> getModulesForUser() {
        String userOid = SecurityUtils.getCurrentUserOid(userService);
        List<Module> modules = moduleRepository.findActiveModulesByUserOid(userOid);
        return moduleMapper.toResponseList(modules);
    }
}
