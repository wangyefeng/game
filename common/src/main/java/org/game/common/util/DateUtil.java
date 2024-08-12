package org.game.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @author: 王叶峰
 * @Date: 2024-07-02
 * @Description: 日期工具类
 */
public abstract class DateUtil {

    /**
     * 默认日期时间格式
     */
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 默认日期格式
     */
    public static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 默认时间格式
     */
    public static final DateTimeFormatter DEFAULT_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static LocalDateTime parseLocalDateTime(String s) {
        return LocalDateTime.parse(s, DEFAULT_DATE_TIME_FORMATTER);
    }

    public static LocalDate parseLocalDate(String s) {
        return LocalDate.parse(s, DEFAULT_DATE_FORMAT);
    }

    public static LocalTime parseLocalTime(String s) {
        return LocalTime.parse(s, DEFAULT_TIME_FORMAT);
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(DEFAULT_DATE_TIME_FORMATTER);
    }

    public static String formatLocalDate(LocalDate localDate) {
        return localDate.format(DEFAULT_DATE_FORMAT);
    }

    public static String formatLocalTime(LocalTime localTime) {
        return localTime.format(DEFAULT_TIME_FORMAT);
    }

    public static long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime toLocalDateTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static long betweenDays(LocalDate start, LocalDate end) {
        return end.toEpochDay() - start.toEpochDay();
    }
}
