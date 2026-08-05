package com.teachermanagement.teacher_management.common.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.teachermanagement.teacher_management.common.constant.BaseAppConstant;
import com.teachermanagement.teacher_management.common.exception.CommonException;
import com.teachermanagement.teacher_management.common.exception.CommonException.SubError;
import com.teachermanagement.teacher_management.common.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@JsonInclude(Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDTO<T> {
  private int status;
  private String message;
  private String code;
  private List<SubError> subErrors;
  private T data;
  private LocalDateTime timestamp;

  public static <T> ResponseDTO<T> failure(
      HttpStatus status, String code, String message, List<SubError> subErrors) {
    return ResponseDTO.<T>builder()
        .status(status.value())
        .code(code)
        .message(message)
        .subErrors(subErrors)
        .timestamp(DateTimeUtils.now())
        .build();
  }

  public static <T> ResponseDTO<T> failure(HttpStatus status, String code, String message) {
    return ResponseDTO.<T>failure(status, code, message, null);
  }

  public static <T> ResponseDTO<T> failure(HttpStatus status, CommonException exception) {
    return failure(status, exception.getCode(), exception.getMessage(), exception.getSubErrors());
  }

  public static <T> ResponseDTO<T> success(T data, String message) {
    return ResponseDTO.<T>builder()
        .status(HttpStatus.OK.value())
        .message(message)
        .data(data)
        .timestamp(DateTimeUtils.now())
        .build();
  }

  public static <T> ResponseDTO<T> success(T data) {
    return ResponseDTO.<T>success(data, BaseAppConstant.DEFAULT_SUCCESS_MESSAGE);
  }

  public static <T> ResponseDTO<T> success() {
    return ResponseDTO.<T>success(null, BaseAppConstant.DEFAULT_SUCCESS_MESSAGE);
  }
}
