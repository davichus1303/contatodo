package com.contatodo.domain.model;

/**
 * Value object representing a company identifier used for multi-tenancy
 * isolation.
 *
 * <p>Immutable once created; a {@code null} value means "no company" (root
 * context).</p>
 */
public record CompanyOid(String value) {

    /**
     * Creates a company identifier value object.
     *
     * @param v Raw company identifier.
     * @return Company identifier value object.
     */
    public static CompanyOid of(String v) {
        return new CompanyOid(v);
    }
}