package com.teachermanagement.teacher_management.common.exception;

import java.io.Serial;
import java.util.Map;

public class NotFoundException extends CommonException {

  @Serial private static final long serialVersionUID = -1970252359543200197L;

  public NotFoundException(String errorCode, Map<String, Object> args) {
    super(errorCode, args);
  }

  public NotFoundException(String errorCode) {
    super(errorCode);
  }
}
