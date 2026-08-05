package com.teachermanagement.teacher_management.common.interfaces;

import java.util.Map;

public interface IResourceDTO {
  default Map<String, String> mapResourceFields() {
    return Map.of();
  }
}
