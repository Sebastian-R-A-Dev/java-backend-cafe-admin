package com.inn.cafe.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final String STANDARD_FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";

    /**
     * @apiNote Transform a Date variable to the format 2025-01-19 14:35:48 it isn't a ISO format this represent a common Date
     * @return String with the standard format Date
     */
    public static String formatDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(STANDARD_FORMAT_DATE);
        return now.format(formatter);
    }
}
