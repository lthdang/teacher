package com.teacher.common.exception;

import java.io.Serial;
import java.util.Map;

public class BadRequestException extends CommonException {

  @Serial private static final long serialVersionUID = -1970256659543200197L;

  public BadRequestException(String errorCode, Map<String, Object> args) {
    super(errorCode, args);
  }

  public BadRequestException(String errorCode) {
    super(errorCode);
  }
}
