package com.teacher.common.exception;


import java.io.Serial;
import java.util.Map;

import com.teacher.common.constant.IBaseErrorCode;

public class UnexpectedException extends CommonException {

  @Serial private static final long serialVersionUID = 5536054975420079450L;

  public UnexpectedException(Map<String, Object> args) {
    super(IBaseErrorCode.ERROR_UNEXPECTED_ERROR, args);
  }

  public UnexpectedException() {
    super(IBaseErrorCode.ERROR_UNEXPECTED_ERROR);
  }

  public UnexpectedException(String errorCode, Map<String, Object> args) {
    super(errorCode, args);
  }

  public UnexpectedException(String errorCode) {
    super(errorCode);
  }
}
