package com.teachermanagement.teacher_management.common.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.teachermanagement.teacher_management.common.constant.ETranslationGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("Translation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class CachedTranslation {
  @Id @Indexed private String id; // sha256(namespace:key:language)

  @Indexed private String key; // translation key

  @Indexed private String language; // language code

  private ETranslationGroup group;

  @Indexed private String namespace;

  private String value;
}
