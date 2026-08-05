package com.teacher.common.exception;

import com.teacher.common.constant.IBaseErrorCode;
import java.io.Serial;
import java.util.Map;

public class BadCredentialsException extends CommonException {

  @Serial private static final long serialVersionUID = 5536024275420079450L;

  public BadCredentialsException(Map<String, Object> args) {
    super(IBaseErrorCode.ERROR_USERNAME_PASSWORD_INVALID, args);
  }

  public BadCredentialsException() {
    super(IBaseErrorCode.ERROR_USERNAME_PASSWORD_INVALID);
  }

  public BadCredentialsException(String errorCode, Map<String, Object> args) {
    super(errorCode, args);
  }

  public BadCredentialsException(String errorCode) {
    super(errorCode);
  }
}
