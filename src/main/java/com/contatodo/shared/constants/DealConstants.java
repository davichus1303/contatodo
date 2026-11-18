package com.contatodo.shared.constants;

/**
 * Constants for deal messages.
 */
public final class DealConstants {

    public static final String CREATED_SUCCESS = "Deal created successfully.";
    public static final String UPDATED_SUCCESS = "Deal updated successfully.";
    public static final String UPDATE_ERROR = "The deal could not be updated";
    public static final String NOT_FOUND_ERROR = "Deal not found";
    public static final String ALREADY_EXISTS_ERROR = "A deal is already registered for that product within the company";
    public static final String COMPANY_OID_REQUIRED = "Company identifier is required";
    public static final String PRODUCT_OID_REQUIRED = "Product identifier is required";
    public static final String UNIT_OF_MEASURE_OID_REQUIRED = "Unit of measure identifier is required";
    public static final String PRICE_REQUIRED = "Price is required";
    public static final String QUANTITY_REQUIRED = "Quantity is required";
    public static final String PRICE_INVALID = "Price must be a valid number";
    public static final String QUANTITY_INVALID = "Quantity must be a valid number";
    public static final String OID_REQUIRED = "Deal identifier is required";

    public static final String CACHE_DEALS_BY_COMPANY = "dealsByCompany";
    public static final String CACHE_ACTIVE_NOT_DELETED_DEALS_BY_COMPANY = "activeNotDeletedDealsByCompany";
    public static final String CACHE_DEAL_OFFER_BY_PRODUCT = "dealOfferByProduct";

    private DealConstants() {
    }
}
