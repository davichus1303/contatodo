package com.contatodo.shared.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Date/time helpers shared across application layers.
 */
public final class DateUtils {

    private DateUtils() {
    }

    /**
     * Gets the start of the given day (midnight).
     *
     * @param date Day whose start is requested.
     * @return Start of the day.
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * Gets the end of the given day (one second before midnight).
     *
     * @param date Day whose end is requested.
     * @return End of the day.
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(23, 59, 59);
    }
}