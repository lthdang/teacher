package com.teacher.common.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.teacher.common.constant.BaseAppConstant;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class CommonException extends RuntimeException {

  @Serial private static final long serialVersionUID = 1042656011775968075L;

  private final String code;

  @Setter private String message;

  @JsonIgnore private Map<String, Object> args;

  private List<SubError> subErrors;

  /**
   * Create exception with error code and default message
   *
   * @param code
   */
  protected CommonException(String code) {
    this.code = code;
    this.message = BaseAppConstant.DEFAULT_ERROR_MESSAGE;
    this.subErrors = null;
    this.args = null;
  }

  /**
   * Create exception with error code and args
   *
   * @param code
   * @param args
   */
  protected CommonException(String code, Map<String, Object> args) {
    this(code);
    this.args = args;
  }

  /**
   * Create exception with list of errors
   *
   * @param errors
   */
  public CommonException setSubErrors(List<SubError> errors) {
    this.subErrors = new ArrayList<>();
    this.subErrors.addAll(errors);
    return this;
  }

  /**
   * Add more error with code and message
   *
   * @param code
   * @return
   */
  public CommonException addSubError(String code, Map<String, Object> args) {
    if (Objects.isNull(this.subErrors)) {
      this.subErrors = new ArrayList<>();
    }
    this.subErrors.add(SubError.with(code, args));
    return this;
  }

  /**
   * Add more error with code and default message
   *
   * @param code
   * @return
   */
  public CommonException addSubError(String code) {
    return addSubError(code);
  }

  @AllArgsConstructor
  @Getter
  public static class SubError {
    private String code;

    @Setter private String message;

    @JsonIgnore private Map<String, Object> args;

    /**
     * Create sub error with errorCode and args
     *
     * @param code
     * @param args
     * @return
     */
    public static SubError with(String code, Map<String, Object> args) {
      return new SubError(code, BaseAppConstant.DEFAULT_ERROR_MESSAGE, args);
    }

    /**
     * Create sub error with errorCode and default message
     *
     * @param code
     * @return
     */
    public static SubError with(String code) {
      return new SubError(code, BaseAppConstant.DEFAULT_ERROR_MESSAGE, null);
    }
  }
}
