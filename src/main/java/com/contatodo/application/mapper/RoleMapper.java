package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateRoleRequest;
import com.contatodo.application.dto.request.UpdateRoleRequest;
import com.contatodo.application.dto.response.RoleResponse;
import com.contatodo.domain.entities.Role;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper for role entities and DTOs.
 */
@Component
public class RoleMapper {

    /**
     * Maps a create request to a domain entity.
     *
     * @param request Create role request.
     * @param userOid Authenticated user identifier.
     * @return Role entity.
     */
    public Role toEntity(CreateRoleRequest request, String userOid) {
        Role role = new Role();
        role.setName(request.getName());
        role.setPermissions(request.getPermissions());
        role.setIsDeleted(false);
        role.setIsActive(true);
        role.setCreatedDate(LocalDateTime.now());
        role.setUpdatedDate(LocalDateTime.now());
        role.setCreatedBy(userOid);
        return role;
    }

    /**
     * Maps a role entity to a response DTO.
     *
     * @param role Role entity.
     * @return Role response.
     */
    public RoleResponse toResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setPermissions(role.getPermissions());
        response.setIsDeleted(role.getIsDeleted());
        response.setIsActive(role.getIsActive());
        response.setCreatedDate(role.getCreatedDate());
        response.setUpdatedDate(role.getUpdatedDate());
        response.setCreatedBy(role.getCreatedBy());
        return response;
    }

    /**
     * Maps a list of roles to response DTOs.
     *
     * @param roles Role entities.
     * @return Role responses.
     */
    public List<RoleResponse> toResponseList(List<Role> roles) {
        return roles.stream().map(this::toResponse).toList();
    }

    /**
     * Updates a role entity with data from an update request.
     * Only updates fields that are not null in the request.
     *
     * @param role Role entity to update.
     * @param request Update role request.
     */
    public void updateEntity(Role role, UpdateRoleRequest request) {
        if (request.getName() != null) {
            role.setName(request.getName());
        }
        if (request.getPermissions() != null) {
            role.setPermissions(request.getPermissions());
        }
        role.setUpdatedDate(LocalDateTime.now());
    }
}
