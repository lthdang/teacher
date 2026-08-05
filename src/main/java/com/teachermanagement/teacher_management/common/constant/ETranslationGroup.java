package com.teachermanagement.teacher_management.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ETranslationGroup {
  TG_DEFAULT("global"),
  TG_MASTER_DATA("masterData"),
  TG_SYSTEM("system");

  private String defaultNamespace;

  public static ETranslationGroup fromNamespace(String namespace) {
    for (ETranslationGroup group : ETranslationGroup.values()) {
      if (group.getDefaultNamespace().equals(namespace)) {
        return group;
      }
    }
    return TG_DEFAULT;
  }
}
