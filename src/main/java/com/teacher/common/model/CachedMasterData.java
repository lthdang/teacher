package com.teacher.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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

@RedisHash("MasterData")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class CachedMasterData {
  @Id private String code;

  private String name;

  @Indexed private String type;

  private Boolean enableHistory;

  private String config;

  private Boolean isActive;

  private CachedMasterData parent;

  private LocalDateTime createdDate;

  private List<CachedMasterData> histories;

  private Integer displayOrder;

  private LocalDateTime modifiedDate;
}
