package com.contatodo.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MongoDB document representing a module.
 */
@Document(collection = "modules")
public class ModuleDocument {

    @Id
    private String id;
    private String name;
    private String link;
    private Boolean isActive;
    private Boolean isDelete;
    private List<ModulePermissionDocument> permissions;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    /**
     * Creates an empty module document.
     */
    public ModuleDocument() {
    }

    /**
     * Creates a module document with specified values.
     *
     * @param id Module identifier.
     * @param name Module name.
     * @param link Module link.
     * @param isActive Active status.
     * @param isDelete Deletion status.
     * @param permissions List of permissions.
     * @param createdDate Creation date.
     * @param updatedDate Last update date.
     */
    public ModuleDocument(String id, String name, String link, Boolean isActive, Boolean isDelete,
                          List<ModulePermissionDocument> permissions, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.isActive = isActive;
        this.isDelete = isDelete;
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

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    public List<ModulePermissionDocument> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<ModulePermissionDocument> permissions) {
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
