package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateRoleRequest;
import com.contatodo.application.dto.request.UpdateRoleRequest;
import com.contatodo.application.dto.response.RoleResponse;
import com.contatodo.application.mapper.RoleMapper;
import com.contatodo.domain.entities.Role;
import com.contatodo.domain.repositories.RoleRepository;
import com.contatodo.shared.exceptions.InvalidRequestException;
import com.contatodo.application.port.AuthenticatedUserProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service containing role business logic.
 */
@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    /**
     * Creates a role service.
     *
     * @param roleRepository Role repository port.
     * @param roleMapper Role mapper.
     * @param authenticatedUserProvider Authenticated user provider.
     */
    public RoleService(
            RoleRepository roleRepository,
            RoleMapper roleMapper,
            AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    /**
     * Creates a new role.
     *
     * @param request Create role request.
     * @return Created role response.
     */
    public RoleResponse createRole(CreateRoleRequest request) {
        String userOid = authenticatedUserProvider.getCurrentUserOid();
        
        Role role = roleMapper.toEntity(request, userOid);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toResponse(savedRole);
    }

    /**
     * Retrieves all active roles.
     *
     * @return List of role responses.
     */
    public List<RoleResponse> getRoles() {
        List<Role> roles = roleRepository.findAllNotDeleted();
        return roleMapper.toResponseList(roles);
    }

    /**
     * Updates an existing role.
     *
     * @param id Role identifier.
     * @param request Update role request.
     * @return Updated role response.
     */
    public RoleResponse updateRole(String id, UpdateRoleRequest request) {
        Optional<Role> roleOptional = roleRepository.findById(id);
        if (roleOptional.isEmpty()) {
            throw new InvalidRequestException("Role not found", List.of("Role not found"));
        }

        Role role = roleOptional.get();
        
        if (role.getIsDeleted()) {
            throw new InvalidRequestException("Cannot update a deleted role", List.of("Cannot update a deleted role"));
        }

        Role updatedRole = roleRepository.save(roleMapper.updateEntity(role, request));
        return roleMapper.toResponse(updatedRole);
    }

    /**
     * Logically deletes a role.
     *
     * @param id Role identifier.
     */
    public void deleteRole(String id) {
        Optional<Role> roleOptional = roleRepository.findById(id);
        if (roleOptional.isEmpty()) {
            throw new InvalidRequestException("Role not found", List.of("Role not found"));
        }

        Role role = roleOptional.get();
        
        if (role.getIsDeleted()) {
            throw new InvalidRequestException("Role already deleted", List.of("Role already deleted"));
        }

        // Perform logical deletion
        roleRepository.save(role.markDeleted());
    }
}
