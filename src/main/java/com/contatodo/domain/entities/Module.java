package com.contatodo.domain.entities;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain entity representing a module in the application.
 */
public class Module {

    private String id;
    private String name;
    private String link;
    private Boolean isActive;
    private Boolean isDeleted;
    private List<ModulePermission> permissions;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    /**
     * Creates an empty module.
     */
    public Module() {
    }

    /**
     * Creates a module with specified values.
     *
     * @param id Module identifier.
     * @param name Module name.
     * @param link Module link.
     * @param isActive Active status.
     * @param isDeleted Deletion status.
     * @param permissions List of permissions.
     * @param createdDate Creation date.
     * @param updatedDate Last update date.
     */
    public Module(String id, String name, String link, Boolean isActive, Boolean isDeleted, 
                  List<ModulePermission> permissions, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
        this.permissions = permissions;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public List<ModulePermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<ModulePermission> permissions) {
        this.permissions = permissions;
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
}
