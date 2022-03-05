package com.example.hairstyle.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeUtils {
    private static final String DATE_PATTERN = "dd-MM-yyyy HH:mm:ss";

    public static Instant parseStringToDateTime(String time) {
        return LocalDateTime
                .parse(time, DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.CHINESE))
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .toInstant();
    }
}
