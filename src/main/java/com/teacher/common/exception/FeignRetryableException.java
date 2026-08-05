package com.teacher.common.exception;


import feign.Request;
import feign.Response;
import feign.RetryableException;
import java.io.Serial;
import java.util.Date;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import com.teacher.common.constant.IBaseErrorCode;

@Getter
public class FeignRetryableException extends RetryableException {
  @Serial private static final long serialVersionUID = 1970252759543200197L;

  private int status;
  private String code;

  public FeignRetryableException(
      int status,
      String message,
      Request.HttpMethod httpMethod,
      Throwable cause,
      Date retryAfter,
      Request request) {
    super(status, message, httpMethod, cause, retryAfter, request);
  }

  public FeignRetryableException(
      int status, String message, Request.HttpMethod httpMethod, Date retryAfter, Request request) {
    super(status, message, httpMethod, retryAfter, request);
  }

  public FeignRetryableException(Response response) {
    super(
        response.status(),
        response.reason(),
        response.request().httpMethod(),
        new Date(),
        response.request());
    this.status = response.status();
    this.code = IBaseErrorCode.ERROR_UNEXPECTED_ERROR;
    if (response.status() == HttpStatus.SERVICE_UNAVAILABLE.value()) {
      this.code = IBaseErrorCode.ERROR_SERVICE_UNAVAILABLE;
    }
  }
}
