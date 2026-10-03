package com.contatodo.infrastructure.security;

import com.contatodo.application.dto.response.RolePermissionResponse;
import com.contatodo.application.dto.response.RoleResponse;
import com.contatodo.application.mapper.RoleMapper;
import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.application.port.CompanyContextProvider;
import com.contatodo.application.port.ModulePermissionChecker;
import com.contatodo.domain.entities.Module;
import com.contatodo.domain.model.ModulePermissionAction;
import com.contatodo.domain.repositories.ModuleRepository;
import com.contatodo.domain.repositories.RoleRepository;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.exceptions.AccessDeniedException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Verifies module permissions for the role of the current session.
 *
 * <p>The permissions are taken from the {@code permissionOfRole} claim that
 * login already issues, which costs no query. When the claim carries no
 * information, a token issued before the role was granted its permissions for
 * example, the permissions stored for the role are read instead, through the
 * same {@link RoleMapper} that builds the response the claim carries. Both
 * paths end in {@link RolePermissionResponse}, so a single check serves
 * them.</p>
 *
 * <p>Modules are matched through {@link ModuleRepository#findAllActive()}, so a
 * link is never hardcoded to an identifier that differs per database. Root
 * sessions are not filtered because their role holds every module.</p>
 */
@Component
public class ModulePermissionCheckerAdapter implements ModulePermissionChecker {

    private final JwtService jwtService;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final CompanyContextProvider companyContextProvider;
    private final ObjectMapper objectMapper;

    /**
     * Creates a module permission checker.
     *
     * @param jwtService JWT service.
     * @param moduleRepository Module repository.
     * @param userRepository User repository.
     * @param roleRepository Role repository.
     * @param roleMapper Role mapper.
     * @param authenticatedUserProvider Authenticated user provider.
     * @param companyContextProvider Company context provider.
     * @param objectMapper Mapper used to read the permission claim.
     */
    public ModulePermissionCheckerAdapter(
            JwtService jwtService,
            ModuleRepository moduleRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            RoleMapper roleMapper,
            AuthenticatedUserProvider authenticatedUserProvider,
            CompanyContextProvider companyContextProvider,
            ObjectMapper objectMapper
    ) {
        this.jwtService = jwtService;
        this.moduleRepository = moduleRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
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
        return currentPermissions().stream()
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
     * Resolves the permissions granted to the role of the current session.
     *
     * @return Granted permissions, empty when neither source provides them.
     */
    private List<RolePermissionResponse> currentPermissions() {
        List<RolePermissionResponse> fromClaim = permissionsFromClaim();
        return fromClaim.isEmpty() ? permissionsFromStoredRole() : fromClaim;
    }

    /**
     * Reads the permissions carried by the token of the current request.
     *
     * @return Granted permissions, empty when the claim is absent or malformed.
     */
    private List<RolePermissionResponse> permissionsFromClaim() {
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

    /**
     * Reads the permissions stored for the role of the current session.
     *
     * <p>Used when the token carries none, so a permission granted after the
     * token was issued takes effect without forcing a new login.</p>
     *
     * @return Stored permissions, empty when the session has no resolvable role.
     */
    private List<RolePermissionResponse> permissionsFromStoredRole() {
        return userRepository.findActiveUserByEmail(authenticatedUserProvider.getCurrentUserEmail(), false)
                .map(user -> user.getRoleId())
                .filter(roleId -> roleId != null && !roleId.isBlank())
                .flatMap(roleRepository::findById)
                .map(role -> toPermissions(roleMapper.toResponse(role)))
                .orElse(List.of());
    }

    private static List<RolePermissionResponse> toPermissions(RoleResponse role) {
        return role.getPermissions() != null ? role.getPermissions() : List.of();
    }
}
