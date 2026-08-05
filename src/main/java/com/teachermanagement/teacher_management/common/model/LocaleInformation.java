package com.teachermanagement.teacher_management.common.model;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import com.teachermanagement.teacher_management.common.constant.BaseAppConstant;

@Data
@Builder
public class LocaleInformation {
  private TimeZone timezone;
  private Locale locale;

  public TimeZone getTimezone() {
    return Optional.ofNullable(timezone)
        .orElse(TimeZone.getTimeZone(BaseAppConstant.DEFAULT_TIMEZONE));
  }

  public String getLanguageCode() {
    return Optional.ofNullable(locale)
        .map(Locale::getLanguage)
        .map(String::toUpperCase)
        .orElse(BaseAppConstant.DEFAULT_LANGUAGE);
  }

  public static LocaleInformation defaultLocale() {
    return LocaleInformation.builder()
        .timezone(TimeZone.getTimeZone(BaseAppConstant.DEFAULT_TIMEZONE))
        .locale(Locale.forLanguageTag(BaseAppConstant.DEFAULT_LANGUAGE))
        .build();
  }

  public static LocaleInformation from(ServerHttpRequest request) {
    TimeZone timezone =
        request.getHeaders().getOrEmpty(BaseAppConstant.DEFAULT_HEADER_TIMEZONE).stream()
            .findFirst()
            .map(TimeZone::getTimeZone)
            .orElse(TimeZone.getTimeZone(BaseAppConstant.DEFAULT_TIMEZONE));
    Locale locale =
        request.getHeaders().getOrEmpty(HttpHeaders.ACCEPT_LANGUAGE).stream()
            .findFirst()
            .map(Locale::forLanguageTag)
            .orElse(Locale.forLanguageTag(BaseAppConstant.DEFAULT_LANGUAGE));
    return LocaleInformation.builder().timezone(timezone).locale(locale).build();
  }

  public static LocaleInformation from(HttpServletRequest request) {
    TimeZone timezone =
        Optional.ofNullable(request.getHeader(BaseAppConstant.DEFAULT_HEADER_TIMEZONE))
            .map(TimeZone::getTimeZone)
            .orElse(TimeZone.getTimeZone(BaseAppConstant.DEFAULT_TIMEZONE));
    Locale locale =
        Optional.ofNullable(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE))
            .map(Locale::forLanguageTag)
            .orElse(Locale.forLanguageTag(BaseAppConstant.DEFAULT_LANGUAGE));
    return LocaleInformation.builder().timezone(timezone).locale(locale).build();
  }
}
