package com.contatodo.application.dto.request;

/**
 * Request DTO for creating a unit of measure.
 */
public class CreateUnitOfMeasureRequest {

    private String name;
    private String abrev;
    private String companyOid;
    private Boolean isDivisible;
    private Boolean isPack;

    public CreateUnitOfMeasureRequest() {
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
