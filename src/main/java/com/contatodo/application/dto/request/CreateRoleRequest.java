package com.contatodo.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request DTO for creating a role.
 */
public class CreateRoleRequest {

    @NotBlank(message = "Role name is required")
    private String name;

    @NotEmpty(message = "Permissions are required")
    @Valid
    private List<RolePermissionRequest> permissions;

    /**
     * Creates an empty create role request.
     */
    public CreateRoleRequest() {
    }

    /**
     * Creates a create role request with specified values.
     *
     * @param name Role name.
     * @param permissions List of role permissions.
     */
    public CreateRoleRequest(String name, List<RolePermissionRequest> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RolePermissionRequest> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<RolePermissionRequest> permissions) {
        this.permissions = permissions;
    }
}
