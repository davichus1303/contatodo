package com.contatodo.shared.constants;

/**
 * Constants for sale module messages.
 */
public final class SaleConstants {

    public static final String SALE_CREATED = "Sale created successfully.";
    public static final String PRODUCT_NOT_FOUND = "Product not found.";
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String USER_NOT_AUTHENTICATED = "User not authenticated.";
    public static final String PRODUCT_OUT_OF_STOCK = "Product is out of stock.";
    public static final String INSUFFICIENT_STOCK = "Insufficient stock for requested quantity.";
    public static final String SALE_WITHOUT_PROFIT = "Sale must generate profit (total sale price must be greater than total cost).";
    public static final String SALE_PRODUCT_OID_REQUIRED = "Product identifier is required.";
    public static final String SALE_QUANTITY_REQUIRED = "Quantity is required.";
    public static final String SALE_TOTAL_SALE_PRICE_REQUIRED = "Total sale price is required.";

    private SaleConstants() {
    }
}
