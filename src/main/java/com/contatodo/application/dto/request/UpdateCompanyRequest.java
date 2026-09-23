package com.contatodo.application.dto.request;

/**
 * Request DTO for updating a company.
 *
 * <p>Only the fields present in the request are applied; a null field keeps
 * the current persisted value.</p>
 */
public class UpdateCompanyRequest {

    private String name;
    private String rfc;
    private String webSite;
    private String ubication;
    private String contactUserOId;
    private Boolean isActive;

    public UpdateCompanyRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getUbication() {
        return ubication;
    }

    public void setUbication(String ubication) {
        this.ubication = ubication;
    }

    public String getContactUserOId() {
        return contactUserOId;
    }

    public void setContactUserOId(String contactUserOId) {
        this.contactUserOId = contactUserOId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
