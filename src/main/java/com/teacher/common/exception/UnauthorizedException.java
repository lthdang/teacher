package com.teacher.common.exception;

import com.teacher.common.constant.IBaseErrorCode;
import java.io.Serial;
import java.util.Map;

public class UnauthorizedException extends CommonException {

  @Serial private static final long serialVersionUID = 5536054275420079450L;

  public UnauthorizedException(Map<String, Object> args) {
    super(IBaseErrorCode.ERROR_UNAUTHORIZED, args);
  }

  public UnauthorizedException() {
    super(IBaseErrorCode.ERROR_UNAUTHORIZED);
  }

  public UnauthorizedException(String errorCode, Map<String, Object> args) {
    super(errorCode, args);
  }

  public UnauthorizedException(String errorCode) {
    super(errorCode);
  }
}
