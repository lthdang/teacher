package com.teachermanagement.teacher_management.common.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.TimeZone;

public class DateTimeUtils {
  public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
  public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
  public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

  public static final String DEFAULT_EXCEL_DATE_FORMAT = "dd/MM/yyyy";

  public static final String DEFAULT_EXCEL_DATETIME_FORMAT = "dd/MM/yyyy HH:mm";

  public static final String DEFAULT_CLIENT_DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

  public static LocalDateTime now() {
    return LocalDateTime.now(ZoneOffset.UTC);
  }

  public static String toISO8601(LocalDateTime date) {
    if (Objects.isNull(date)) return null;
    return DateTimeFormatter.ISO_DATE_TIME.format(date);
  }

  public static String format(LocalDateTime date, String format) {
    if (Objects.isNull(date)) return null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return formatter.format(date);
  }

  public static LocalDate parseDate(String date, String format) {
    if (Objects.isNull(date)) return null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDate.parse(date, formatter);
  }

  public static LocalTime parseTime(String time, String format) {
    if (Objects.isNull(time)) return null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalTime.parse(time, formatter);
  }

  public static LocalDateTime parseDateTime(String date, String format) {
    if (Objects.isNull(date)) return null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDateTime.parse(date, formatter);
  }

  public static LocalDateTime convertTimezone(LocalDateTime date, TimeZone fromTz, TimeZone toTz) {
    if (Objects.isNull(date) || Objects.isNull(fromTz) || Objects.isNull(toTz)) return date;
    return date.atZone(fromTz.toZoneId()).withZoneSameInstant(toTz.toZoneId()).toLocalDateTime();
  }

  public static LocalDateTime toUtcFromTimezone(LocalDateTime date, TimeZone fromTz) {
    return convertTimezone(date, fromTz, TimeZone.getTimeZone("UTC"));
  }

  public static LocalDateTime toTimezoneFromUtc(LocalDateTime date, TimeZone toTz) {
    return convertTimezone(date, TimeZone.getTimeZone("UTC"), toTz);
  }

  public static LocalDateTime getStartOfDay(LocalDate date) {
    return date.atStartOfDay();
  }

  public static LocalDateTime getEndOfDay(LocalDate date) {
    LocalTime endTime = LocalTime.of(23, 59, 59, 999_000_000);
    return LocalDateTime.of(date, endTime);
  }

  public static LocalDateTime getFirstDayOfWeek(LocalDate date) {
    int daysToAdd = DayOfWeek.MONDAY.getValue() - date.getDayOfWeek().getValue();
    return getStartOfDay(date.plusDays(daysToAdd));
  }

  public static LocalDateTime getLastDayOfWeek(LocalDate date) {
    int daysToAdd = DayOfWeek.SUNDAY.getValue() - date.getDayOfWeek().getValue();
    return getEndOfDay(date.plusDays(daysToAdd));
  }

  public static LocalDateTime getFirstDayOfMonth(LocalDate date) {
    return date.withDayOfMonth(1).atStartOfDay();
  }

  public static LocalDateTime getLastDayOfMonth(LocalDate date) {
    YearMonth yearMonth = YearMonth.from(date);
    return getEndOfDay(yearMonth.atEndOfMonth());
  }

  public static LocalDateTime getFirstDayOfYear(LocalDate date) {
    return LocalDate.of(date.getYear(), Month.JANUARY, 1).atStartOfDay();
  }

  public static LocalDateTime getLastDayOfYear(LocalDate date) {
    int year = date.getYear();
    return getEndOfDay(LocalDate.of(year, 12, 31));
  }
}
