package com.contatodo.application.dto.request;

/**
 * Request DTO for updating an acquisition type.
 */
public class UpdateAcquisitionTypeRequest {

    private String name;
    private String description;
    private Boolean isActive;
    private Boolean affectsInventory;

    public UpdateAcquisitionTypeRequest() {
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getAffectsInventory() {
        return affectsInventory;
    }

    public void setAffectsInventory(Boolean affectsInventory) {
        this.affectsInventory = affectsInventory;
    }
}
