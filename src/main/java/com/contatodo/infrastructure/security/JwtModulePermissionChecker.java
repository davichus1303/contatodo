package com.contatodo.infrastructure.security;

import com.contatodo.application.dto.response.RolePermissionResponse;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.application.port.ModulePermissionChecker;
import com.contatodo.domain.entities.Module;
import com.contatodo.domain.model.ModulePermissionAction;
import com.contatodo.domain.repositories.ModuleRepository;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.exceptions.AccessDeniedException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Verifies module permissions from the {@code permissionOfRole} claim carried
 * by the session token.
 *
 * <p>The claim is written at login with one entry per module granted to the
 * role, and each entry is a {@link RolePermissionResponse}, the same payload
 * the roles endpoints return. Modules are matched through
 * {@link ModuleRepository#findAllActive()}, so a link is never hardcoded to an
 * identifier that differs per database.</p>
 *
 * <p>Root sessions are not filtered because their role holds every module.</p>
 */
@Component
public class JwtModulePermissionChecker implements ModulePermissionChecker {

    private final JwtService jwtService;
    private final ModuleRepository moduleRepository;
    private final CompanyContextProvider companyContextProvider;
    private final ObjectMapper objectMapper;

    /**
     * Creates a JWT module permission checker.
     *
     * @param jwtService JWT service.
     * @param moduleRepository Module repository.
     * @param companyContextProvider Company context provider.
     * @param objectMapper Mapper used to read the permission claim.
     */
    public JwtModulePermissionChecker(
            JwtService jwtService,
            ModuleRepository moduleRepository,
            CompanyContextProvider companyContextProvider,
            ObjectMapper objectMapper
    ) {
        this.jwtService = jwtService;
        this.moduleRepository = moduleRepository;
        this.companyContextProvider = companyContextProvider;
        this.objectMapper = objectMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(String moduleLink, ModulePermissionAction action) {
        if (companyContextProvider.isRoot()) {
            return true;
        }
        Optional<String> moduleOid = findModuleOid(moduleLink);
        if (moduleOid.isEmpty()) {
            return false;
        }
        return grantedPermissions().stream()
                .filter(permission -> moduleOid.get().equals(permission.getModuleOid()))
                .findFirst()
                .map(permission -> action.isGranted(permission.getPermissions()))
                .orElse(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requirePermission(String moduleLink, ModulePermissionAction action) {
        if (!hasPermission(moduleLink, action)) {
            throw new AccessDeniedException(AuthConstants.ACCESS_DENIED);
        }
    }

    /**
     * Finds the stored identifier of an active module by its link.
     *
     * @param moduleLink Module link.
     * @return Module identifier, empty when no active module uses the link.
     */
    private Optional<String> findModuleOid(String moduleLink) {
        if (moduleLink == null || moduleLink.isBlank()) {
            return Optional.empty();
        }
        return moduleRepository.findAllActive().stream()
                .filter(module -> moduleLink.equals(module.getLink()))
                .map(Module::getId)
                .findFirst();
    }

    /**
     * Reads the permissions granted to the role of the current session.
     *
     * @return Granted permissions, empty when the claim is absent or malformed.
     */
    private List<RolePermissionResponse> grantedPermissions() {
        Object claim = jwtService.getClaimValue(AuthConstants.JWT_CLAIM_PERMISSION_OF_ROLE);
        if (!(claim instanceof List<?> entries) || entries.isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.convertValue(entries, new TypeReference<List<RolePermissionResponse>>() {
            });
        } catch (IllegalArgumentException exception) {
            return List.of();
        }
    }
}
