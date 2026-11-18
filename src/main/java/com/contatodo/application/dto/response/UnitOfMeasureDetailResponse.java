package com.contatodo.application.dto.response;

/**
 * Minimal unit of measure information joined into a product response.
 */
public class UnitOfMeasureDetailResponse {

    private String oid;
    private String name;
    private Boolean isPack;

    public UnitOfMeasureDetailResponse() {
    }

    /**
     * Creates a unit of measure detail.
     *
     * @param oid Unit of measure identifier.
     * @param name Unit of measure name.
     * @param isPack Whether the unit represents a pack.
     */
    public UnitOfMeasureDetailResponse(String oid, String name, Boolean isPack) {
        this.oid = oid;
        this.name = name;
        this.isPack = isPack;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets whether the unit represents a pack.
     *
     * @return True when the unit is a pack.
     */
    public Boolean getIsPack() {
        return isPack;
    }

    public void setIsPack(Boolean isPack) {
        this.isPack = isPack;
    }
}
