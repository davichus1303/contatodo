package com.contatodo.application.dto.response;

/**
 * Minimal unit of measure information joined into a product response.
 */
public class UnitOfMeasureDetailResponse {

    private String oid;
    private String name;

    public UnitOfMeasureDetailResponse() {
    }

    /**
     * Creates a unit of measure detail.
     *
     * @param oid Unit of measure identifier.
     * @param name Unit of measure name.
     */
    public UnitOfMeasureDetailResponse(String oid, String name) {
        this.oid = oid;
        this.name = name;
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
}
