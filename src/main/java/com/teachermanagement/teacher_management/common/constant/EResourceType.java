package com.teachermanagement.teacher_management.common.constant;

import com.google.common.collect.ImmutableMap;
import com.teachermanagement.teacher_management.common.dto.masterdata.MasterDataDTO;
import com.teachermanagement.teacher_management.common.exception.BadRequestException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;

public enum EResourceType {
  IMAGE,
  PDF,
  EXCEL;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  private static class ResourceTypeConfig {
    private List<String> extensions;

    public List<String> getExtensions() {
      return Optional.ofNullable(extensions).orElse(Collections.emptyList());
    }
  }

  public static EResourceType detectResourceType(
      String fileName, List<MasterDataDTO> resourceTypes) {
    String extension = Optional.ofNullable(FilenameUtils.getExtension(fileName)).orElse("");
    String typeCode =
        resourceTypes.stream()
            .filter(
                resourceType ->
                    resourceType
                        .getConfigAs(ResourceTypeConfig.class)
                        .getExtensions()
                        .contains(extension.toLowerCase()))
            .findFirst()
            .map(MasterDataDTO::getCode)
            .orElseThrow(
                () ->
                    new BadRequestException(
                        IBaseErrorCode.ERROR_NOT_SUPPORTED_RESOURCE_EXTENSION,
                        ImmutableMap.of("extension", extension)));
    return EResourceType.valueOf(typeCode);
  }
}
