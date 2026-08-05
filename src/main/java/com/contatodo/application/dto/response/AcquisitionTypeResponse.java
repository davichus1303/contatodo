package com.contatodo.application.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO for acquisition type.
 */
public class AcquisitionTypeResponse {

    private String id;
    private String name;
    private String description;
    private String userOid;
    private Boolean isActive;
    private Boolean isDeleted;
    private Boolean affectsInventory;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public AcquisitionTypeResponse() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserOid() {
        return userOid;
    }

    public void setUserOid(String userOid) {
        this.userOid = userOid;
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

    public Boolean getAffectsInventory() {
        return affectsInventory;
    }

    public void setAffectsInventory(Boolean affectsInventory) {
        this.affectsInventory = affectsInventory;
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
