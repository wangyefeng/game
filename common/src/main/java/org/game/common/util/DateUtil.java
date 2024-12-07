package org.game.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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

    // 使用 ThreadLocal 来确保每个线程都有自己的 SimpleDateFormat 实例
    private static ThreadLocal<DateFormat> threadLocalDateFormat = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    public static LocalDateTime parseLocalDateTime(String s) {
        return LocalDateTime.parse(s, DEFAULT_DATE_TIME_FORMATTER);
    }

    public static LocalDate parseLocalDate(String s) {
        return LocalDate.parse(s, DEFAULT_DATE_FORMAT);
    }

    public static LocalTime parseLocalTime(String s) {
        return LocalTime.parse(s, DEFAULT_TIME_FORMAT);
    }

    public static String format(LocalDateTime localDateTime) {
        return localDateTime.format(DEFAULT_DATE_TIME_FORMATTER);
    }

    public static String format(LocalDate localDate) {
        return localDate.format(DEFAULT_DATE_FORMAT);
    }

    public static String format(LocalTime localTime) {
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

    public static String format(Date date) {
        return threadLocalDateFormat.get().format(date);
    }
}
