package com.teachermanagement.teacher_management.common.resources;

import com.teachermanagement.teacher_management.common.dto.masterdata.MasterDataDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResourceDTO {
  protected Long id;
  protected String name;
  protected String path;
  protected Long size;
  protected LocalDateTime expiredDate;
  protected MasterDataDTO type;
  protected Boolean isPublic;
  protected Boolean isTemporary;
  protected String source;
  protected Boolean isActive;
  protected List<MasterDataDTO> availableSizes;
  protected String extension;
  protected String cacheKey;
}
