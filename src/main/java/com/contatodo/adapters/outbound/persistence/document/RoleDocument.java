package com.contatodo.adapters.outbound.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MongoDB document representing a role.
 */
@Document(collection = "roles")
public class RoleDocument {

    @Id
    private String id;
    private String name;
    private List<RolePermissionDocument> permissions;
    private Boolean isDeleted;
    private Boolean isActive;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;

    /**
     * Creates an empty role document.
     */
    public RoleDocument() {
    }

    /**
     * Creates a role document with specified values.
     *
     * @param id Role identifier.
     * @param name Role name.
     * @param permissions List of permissions.
     * @param isDeleted Deletion status.
     * @param isActive Active status.
     * @param createdDate Creation date.
     * @param updatedDate Last update date.
     * @param createdBy User who created the role.
     */
    public RoleDocument(String id, String name, List<RolePermissionDocument> permissions, Boolean isDeleted, Boolean isActive,
                       LocalDateTime createdDate, LocalDateTime updatedDate, String createdBy) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
        this.isDeleted = isDeleted;
        this.isActive = isActive;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.createdBy = createdBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RolePermissionDocument> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<RolePermissionDocument> permissions) {
        this.permissions = permissions;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
