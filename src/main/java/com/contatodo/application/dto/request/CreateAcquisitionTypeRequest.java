package com.contatodo.application.dto.request;

/**
 * Request DTO for creating an acquisition type.
 */
public class CreateAcquisitionTypeRequest {

    private String name;
    private String description;
    private String userOid;

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

    public String getUserOid() {
        return userOid;
    }

    public void setUserOid(String userOid) {
        this.userOid = userOid;
    }
}
