package com.teacher.common.model;

import com.teacher.common.constant.EResourceType;
import com.teacher.common.interfaces.IModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("Resource")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CachedResource implements IModel {
  @Id @Indexed private Long id;

  private String name;
  private String path;
  private Long size;
  private EResourceType type;
  private LocalDateTime expiredDate;
  private Boolean isPublic;
  private Boolean isTemporary;
  private Integer refCount;
  private Boolean isActive;
  private List<String> availableSizes;
  private String extension;
}
