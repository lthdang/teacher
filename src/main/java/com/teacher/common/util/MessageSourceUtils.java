package com.teacher.common.util;


import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.teacher.common.constant.ETranslationGroup;
import com.teacher.common.interfaces.IBaseTranslationCache;
import com.teacher.common.model.CachedTranslation;
import com.teacher.common.model.CachedTranslationId;
import com.teacher.common.model.LocaleInformation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageSourceUtils {
  private final IBaseTranslationCache translationCache;
  private final LocaleInformation currentLocale;

  private String getMessage(
      String code, Map<String, Object> args, Locale locale, String defaultFallbackString) {
    // Get the translation value from the cache
    String translationValue =
        translationCache
            .findById(
                CachedTranslationId.builder()
                    .namespace(ETranslationGroup.TG_SYSTEM.getDefaultNamespace())
                    .key(code)
                    .language(locale.getLanguage().toUpperCase())
                    .build())
            .map(CachedTranslation::getValue)
            .orElse(defaultFallbackString);
    if (Objects.nonNull(args) && Objects.nonNull(translationValue)) {
      // Replace the placeholders in the translation value with the actual values
      for (Map.Entry<String, Object> entry : args.entrySet()) {
        translationValue =
            translationValue.replace(
                "{{" + entry.getKey() + "}}",
                Optional.ofNullable(entry.getValue()).map(String::valueOf).orElse(""));
      }
    }
    return translationValue;
  }

  public String getMessageOrDefault(
      Locale locale, String code, String defaultFallbackString, Map<String, Object> args) {
    return getMessage(code, args, locale, defaultFallbackString);
  }

  public String getMessage(Locale locale, String code) {
    return getMessage(code, null, locale, code);
  }

  public String getMessage(Locale locale, String code, Map<String, Object> args) {
    return getMessage(code, args, locale, code);
  }

  public String getMessage(String code) {
    return getMessage(code, null, currentLocale.getLocale(), code);
  }

  public String getMessage(String code, Map<String, Object> args) {
    return getMessage(code, args, currentLocale.getLocale(), code);
  }
}
