package com.teachermanagement.teacher_management.common.dto.masterdata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachermanagement.teacher_management.common.model.CachedMasterData;
import com.teachermanagement.teacher_management.common.util.JsonUtils;
import com.teachermanagement.teacher_management.common.util.ObjectMapperUtils;

import java.time.LocalDateTime;
import java.util.Optional;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class MasterDataDTO {
  private static final ObjectMapper mapper = ObjectMapperUtils.getInstance();
  private String code;
  private String name;
  private JsonNode config;
  private MasterDataDTO parent;
  private String type;
  private Boolean isActive;
  private LocalDateTime createdDate;
  private Integer displayOrder;
  private LocalDateTime modifiedDate;

  public static MasterDataDTO from(CachedMasterData data) {
    JsonNode config = Optional.ofNullable(data.getConfig()).map(JsonUtils::toJsonNode).orElse(null);
    return new MasterDataDTO(
        data.getCode(),
        data.getName(),
        config,
        Optional.ofNullable(data.getParent()).map(MasterDataDTO::from).orElse(null),
        data.getType(),
        data.getIsActive(),
        data.getCreatedDate(),
        data.getDisplayOrder(),
        data.getModifiedDate());
  }

  /**
   * Get master data config as specific class
   *
   * @param type
   * @param <T>
   * @return
   */
  public <T> T getConfigAs(Class<T> type) {
    return Optional.ofNullable(this.config)
        .map(config -> mapper.convertValue(config, type))
        .orElse(null);
  }
}
