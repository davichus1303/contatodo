package com.contatodo.application.dto.request;

/**
 * Request DTO for creating an acquisition type.
 */
public class CreateAcquisitionTypeRequest {

    private String name;
    private String description;
    private Boolean affectsInventory;

    public CreateAcquisitionTypeRequest() {
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

    public Boolean getAffectsInventory() {
        return affectsInventory;
    }

    public void setAffectsInventory(Boolean affectsInventory) {
        this.affectsInventory = affectsInventory;
    }
}
