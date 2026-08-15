package com.contatodo.application.services;

import com.contatodo.application.dto.request.CreateRoleRequest;
import com.contatodo.application.dto.request.UpdateRoleRequest;
import com.contatodo.application.dto.response.RoleResponse;
import com.contatodo.application.mapper.RoleMapper;
import com.contatodo.domain.entities.Role;
import com.contatodo.domain.repositories.RoleRepository;
import com.contatodo.shared.exceptions.InvalidRequestException;
import com.contatodo.shared.utils.SecurityUtils;
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
    private final UserService userService;

    /**
     * Creates a role service.
     *
     * @param roleRepository Role repository port.
     * @param roleMapper Role mapper.
     * @param userService User service.
     */
    public RoleService(
            RoleRepository roleRepository,
            RoleMapper roleMapper,
            UserService userService
    ) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.userService = userService;
    }

    /**
     * Creates a new role.
     *
     * @param request Create role request.
     * @return Created role response.
     */
    public RoleResponse createRole(CreateRoleRequest request) {
        String userOid = SecurityUtils.getCurrentUserOid(userService);
        
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
        List<Role> roles = roleRepository.findAllActive();
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

        roleMapper.updateEntity(role, request);
        Role updatedRole = roleRepository.save(role);
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
        role.setIsDeleted(true);
        role.setUpdatedDate(java.time.LocalDateTime.now());
        
        roleRepository.save(role);
    }
}
