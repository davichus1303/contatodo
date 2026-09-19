package com.contatodo.application.mapper;

import com.contatodo.application.dto.request.CreateRoleRequest;
import com.contatodo.application.dto.request.RolePermissionRequest;
import com.contatodo.application.dto.request.UpdateRoleRequest;
import com.contatodo.application.dto.response.RolePermissionResponse;
import com.contatodo.application.dto.response.RoleResponse;
import com.contatodo.domain.entities.Module;
import com.contatodo.domain.entities.Role;
import com.contatodo.domain.entities.RolePermission;
import com.contatodo.domain.repositories.ModuleRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper for role entities and DTOs.
 */
@Component
public class RoleMapper implements ResponseMapper<Role, RoleResponse> {

    private final ModuleRepository moduleRepository;

    /**
     * Creates a role mapper.
     *
     * @param moduleRepository Module repository port.
     */
    public RoleMapper(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

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
        if (permissions == null || permissions.isEmpty()) {
            return List.of();
        }

        // Fetch module names in bulk
        Map<String, String> moduleNames = moduleRepository.findAllById(
                permissions.stream()
                        .map(RolePermission::getModuleOid)
                        .distinct()
                        .toList()
        ).stream()
                .collect(Collectors.toMap(Module::getId, Module::getName));

        return permissions.stream()
                .map(permission -> toPermissionResponse(permission, moduleNames.get(permission.getModuleOid())))
                .toList();
    }

    /**
     * Maps a single domain permission to its response form with module name.
     *
     * @param permission Domain permission.
     * @param moduleName Module name (may be null).
     * @return Response permission.
     */
    private static RolePermissionResponse toPermissionResponse(RolePermission permission, String moduleName) {
        RolePermission.PermissionDetails flags = permission.getPermissions();
        return new RolePermissionResponse(
                permission.getModuleOid(),
                moduleName,
                new RolePermissionResponse.Permissions(
                        flags != null ? flags.getCreate() : null,
                        flags != null ? flags.getUpdate() : null,
                        flags != null ? flags.getDelete() : null,
                        flags != null ? flags.getView() : null
                )
        );
    }
}