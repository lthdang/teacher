package com.teachermanagement.teacher_management.common.exception;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {
  @Override
  public Exception decode(String methodKey, Response response) {
    ErrorDecoder defaultErrorDecoder = new Default();
    Exception exception = defaultErrorDecoder.decode(methodKey, response);

    if (exception instanceof RetryableException) {
      return exception;
    }

    if (response.status() >= 500) {
      return new FeignRetryableException(response);
    }

    return exception;
  }
}
