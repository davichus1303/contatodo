package com.contatodo.application.dto.response;

/**
 * Response DTO for a unit of measure.
 */
public class UnitOfMeasureResponse {

    private String id;
    private String name;
    private String abrev;
    private String companyOid;
    private Boolean isActive;
    private Boolean isDeleted;
    private Boolean isDivisible;
    private Boolean isPack;

    public UnitOfMeasureResponse() {
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

    public String getAbrev() {
        return abrev;
    }

    public void setAbrev(String abrev) {
        this.abrev = abrev;
    }

    public String getCompanyOid() {
        return companyOid;
    }

    public void setCompanyOid(String companyOid) {
        this.companyOid = companyOid;
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

    public Boolean getIsDivisible() {
        return isDivisible;
    }

    public void setIsDivisible(Boolean isDivisible) {
        this.isDivisible = isDivisible;
    }

    public Boolean getIsPack() {
        return isPack;
    }

    public void setIsPack(Boolean isPack) {
        this.isPack = isPack;
    }
}
