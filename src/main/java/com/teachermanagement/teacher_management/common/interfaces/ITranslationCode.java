package com.teachermanagement.teacher_management.common.interfaces;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public interface ITranslationCode {
  Map<String, String> getBaseMessages();

  Map<String, String> getMessages();

  default String getMessage(String code) {
    return Stream.of(getBaseMessages(), getMessages())
        .map(messages -> messages.get(code))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(code);
  }
  ;
}
