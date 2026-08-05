package com.teacher.common.model;


import com.teacher.common.util.HashUtils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CachedTranslationId {
  private String namespace;
  private String key;
  private String language;

  public CachedTranslationId() {}

  @Override
  public String toString() {
    return HashUtils.SHA256((namespace + ":" + key + ":" + language).getBytes());
  }

  public static class Builder {
    private String namespace;
    private String key;
    private String language;

    public Builder namespace(String namespace) {
      this.namespace = namespace;
      return this;
    }

    public Builder key(String key) {
      this.key = key;
      return this;
    }

    public Builder language(String language) {
      this.language = language;
      return this;
    }

    public String build() {
      return new CachedTranslationId(namespace, key, language).toString();
    }
  }

  public static Builder builder() {
    return new CachedTranslationId.Builder();
  }
}
