package com.organizadororcamentopessoal.tempo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

public class Tempo {
    public static long localDateStartInMilli(LocalDate date) {
        ZoneId zone = ZoneId.systemDefault();
        return date.atStartOfDay(zone).toEpochSecond() * 1000;
    }
    public static long localDateEndInMilli(LocalDate date) {
        ZoneId zone = ZoneId.systemDefault();
        return date.plusDays(1).atStartOfDay(zone).toEpochSecond() * 1000 -1;
    }

    public static long getTodayStartInMilli() {
        return localDateStartInMilli(LocalDate.now());
    }

    public static long getTodayEndInMilli() {
        return localDateEndInMilli(LocalDate.now());
    }

    public static Date getTodayStart() {
        return new Date(getTodayStartInMilli());
    }

    public static Date getTodayEnd() {
        return new Date(getTodayEndInMilli());
    }
}
