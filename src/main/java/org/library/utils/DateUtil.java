package org.library.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public static String formatDate(LocalDate date) {
        return date == null ? "" : date.format(formatter);
    }
}
