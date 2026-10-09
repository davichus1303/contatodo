package com.contatodo.application.dto.request;

/**
 * Request DTO for updating a unit of measure.
 */
public class UpdateUnitOfMeasureRequest {

    private String name;
    private String abrev;
    private Boolean isActive;
    private Boolean isDeleted;

    public UpdateUnitOfMeasureRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbrev() {
        return abrev;
    }

    public void setAbrev(String abrev) {
        this.abrev = abrev;
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
}