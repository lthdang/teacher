package com.teacher.common.exception;

import com.teacher.common.constant.IBaseErrorCode;
import java.io.Serial;
import java.util.Map;

public class ForbiddenException extends CommonException {

  @Serial private static final long serialVersionUID = 9039531873856443408L;

  public ForbiddenException(String errorCode) {
    super(errorCode);
  }

  public ForbiddenException(String errorCode, Map<String, Object> args) {
    super(errorCode, args);
  }

  public ForbiddenException(Map<String, Object> args) {
    super(IBaseErrorCode.ERROR_PERMISSION_DENIED, args);
  }

  public ForbiddenException() {
    super(IBaseErrorCode.ERROR_PERMISSION_DENIED);
  }
}
