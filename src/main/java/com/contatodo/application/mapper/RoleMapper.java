package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateRoleRequest;
import com.contatodo.application.dto.request.RolePermissionRequest;
import com.contatodo.application.dto.request.UpdateRoleRequest;
import com.contatodo.application.dto.response.RolePermissionResponse;
import com.contatodo.application.dto.response.RoleResponse;
import com.contatodo.domain.entities.Role;
import com.contatodo.domain.entities.RolePermission;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper for role entities and DTOs.
 */
@Component
public class RoleMapper implements ResponseMapper<Role, RoleResponse> {

    /**
     * Maps a create request to a domain entity.
     *
     * @param request Create role request.
     * @param userOid Authenticated user identifier.
     * @return Role entity.
     */
    public Role toEntity(CreateRoleRequest request, String userOid) {
        LocalDateTime now = LocalDateTime.now();
        return Role.builder()
                .name(request.getName())
                .permissions(toPermissionEntities(request.getPermissions()))
                .isDeleted(false)
                .isActive(true)
                .createdDate(now)
                .updatedDate(now)
                .createdBy(userOid)
                .build();
    }

    /**
     * Produces an updated copy of an existing role applying request changes.
     *
     * @param existing Current persisted role.
     * @param request Update role request.
     * @return New immutable role instance with the changes applied.
     */
    public Role updateEntity(Role existing, UpdateRoleRequest request) {
        return Role.builder()
                .id(existing.getId())
                .name(request.getName() != null ? request.getName() : existing.getName())
                .permissions(request.getPermissions() != null
                        ? toPermissionEntities(request.getPermissions())
                        : existing.getPermissions())
                .isDeleted(existing.getIsDeleted())
                .isActive(request.getIsActive() != null ? request.getIsActive() : existing.getIsActive())
                .createdDate(existing.getCreatedDate())
                .updatedDate(LocalDateTime.now())
                .createdBy(existing.getCreatedBy())
                .build();
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
        response.setPermissions(toPermissionResponses(role.getPermissions()));
        response.setIsDeleted(role.getIsDeleted());
        response.setIsActive(role.getIsActive());
        response.setCreatedDate(role.getCreatedDate());
        response.setUpdatedDate(role.getUpdatedDate());
        response.setCreatedBy(role.getCreatedBy());
        return response;
    }

    /**
     * Maps request permissions to domain permission entities.
     *
     * @param permissions Request permissions.
     * @return Domain permissions.
     */
    private List<RolePermission> toPermissionEntities(List<RolePermissionRequest> permissions) {
        if (permissions == null) {
            return List.of();
        }
        return permissions.stream()
                .map(RoleMapper::toPermissionEntity)
                .toList();
    }

    /**
     * Maps a single request permission to its domain form.
     *
     * @param request Request permission.
     * @return Domain permission.
     */
    private static RolePermission toPermissionEntity(RolePermissionRequest request) {
        RolePermissionRequest.Permissions flags = request.getPermissions();
        return RolePermission.builder()
                .moduleOid(request.getModuleOid())
                .permissions(RolePermission.PermissionDetails.builder()
                        .create(flags != null ? flags.getCreate() : null)
                        .update(flags != null ? flags.getUpdate() : null)
                        .delete(flags != null ? flags.getDelete() : null)
                        .view(flags != null ? flags.getView() : null)
                        .build())
                .build();
    }

    /**
     * Maps domain permissions to response payloads.
     *
     * @param permissions Domain permissions.
     * @return Response permissions.
     */
    private List<RolePermissionResponse> toPermissionResponses(List<RolePermission> permissions) {
        return permissions.stream()
                .map(RoleMapper::toPermissionResponse)
                .toList();
    }

    /**
     * Maps a single domain permission to its response form.
     *
     * @param permission Domain permission.
     * @return Response permission.
     */
    private static RolePermissionResponse toPermissionResponse(RolePermission permission) {
        RolePermission.PermissionDetails flags = permission.getPermissions();
        return new RolePermissionResponse(
                permission.getModuleOid(),
                new RolePermissionResponse.Permissions(
                        flags != null ? flags.getCreate() : null,
                        flags != null ? flags.getUpdate() : null,
                        flags != null ? flags.getDelete() : null,
                        flags != null ? flags.getView() : null
                )
        );
    }
}
