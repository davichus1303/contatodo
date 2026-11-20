package com.contatodo.shared.constants;

/**
 * Constants for expense module.
 */
public final class ExpenseConstants {

    /**
     * Time for the beginning of the day (00:00:00).
     */
    public static final String START_OF_DAY = "00:00:00";

    /**
     * Time for the end of the day (23:59:59).
     */
    public static final String END_OF_DAY = "23:59:59";

    /**
     * Date time format for parsing dates.
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_CURRENCY = "MXN";

    public static final String NAME_REQUIRED = "Name is required";
    public static final String UNIT_OF_MEASURE_REQUIRED = "Unit of measure is required";
    public static final String QUANTITY_GREATER_THAN_ZERO = "Quantity must be greater than 0";
    public static final String AMOUNT_GREATER_THAN_ZERO = "Amount must be greater than 0";
    public static final String CURRENCY_REQUIRED = "Currency is required";

    private ExpenseConstants() {
    }
}
