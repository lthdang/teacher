package com.teachermanagement.teacher_management.common.exception;


import com.google.common.collect.ImmutableMap;
import com.teachermanagement.teacher_management.common.constant.BaseAppConstant;
import com.teachermanagement.teacher_management.common.constant.IBaseErrorCode;
import com.teachermanagement.teacher_management.common.dto.ResponseDTO;
import com.teachermanagement.teacher_management.common.model.LocaleInformation;
import com.teachermanagement.teacher_management.common.util.MessageSourceUtils;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public abstract class BaseGlobalExceptionHandler {
  private final MessageSourceUtils messageSourceUtils;
  private final LocaleInformation currentLocale;

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseDTO<?> handleBadRequestException(
      BadRequestException exception, WebRequest request) {
    return ResponseDTO.failure(HttpStatus.BAD_REQUEST, prepareErrorMessages(exception, request));
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseDTO<?> handleNotFoundException(NotFoundException exception, WebRequest request) {
    return ResponseDTO.failure(HttpStatus.NOT_FOUND, prepareErrorMessages(exception, request));
  }

  @ExceptionHandler(UnexpectedException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ResponseDTO<?> handleUnexpectedException(
      UnexpectedException exception, WebRequest request) {
    return ResponseDTO.failure(
        HttpStatus.INTERNAL_SERVER_ERROR, prepareErrorMessages(exception, request));
  }

  @ExceptionHandler(ForbiddenException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ResponseBody
  public ResponseDTO<?> handleForbiddenException(ForbiddenException exception, WebRequest request) {
    return ResponseDTO.failure(HttpStatus.FORBIDDEN, prepareErrorMessages(exception, request));
  }

  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseDTO<?> handleBindException(BindException exception, WebRequest request) {
    List<FieldError> errors = exception.getBindingResult().getFieldErrors();
    BadRequestException badRequestException =
        new BadRequestException(IBaseErrorCode.ERROR_SUBMIT_DATA_INVALID);
    badRequestException.setSubErrors(
        errors.stream()
            .map(error -> CommonException.SubError.with(error.getDefaultMessage()))
            .toList());
    return ResponseDTO.failure(
        HttpStatus.BAD_REQUEST, prepareErrorMessages(badRequestException, request));
  }

  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseDTO<?> handleValidationException(
      ValidationException exception, WebRequest request) {
    return ResponseDTO.failure(
        HttpStatus.BAD_REQUEST,
        prepareErrorMessages((CommonException) exception.getCause(), request));
  }

  @ExceptionHandler(DisabledException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseDTO<?> handleDisabledException(DisabledException exception, WebRequest request) {
    return ResponseDTO.failure(
        HttpStatus.BAD_REQUEST,
        IBaseErrorCode.ERROR_USER_IS_NOT_AVAILABLE,
        messageSourceUtils.getMessage(
            currentLocale.getLocale(), IBaseErrorCode.ERROR_USER_IS_NOT_AVAILABLE));
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseDTO<?> handleBadCredentialException(
      BadCredentialsException exception, WebRequest request) {
    return ResponseDTO.failure(
        HttpStatus.BAD_REQUEST,
        IBaseErrorCode.ERROR_USERNAME_PASSWORD_INVALID,
        messageSourceUtils.getMessage(
            currentLocale.getLocale(), IBaseErrorCode.ERROR_USERNAME_PASSWORD_INVALID));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseDTO<?> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException exception, WebRequest request) {
    return ResponseDTO.failure(
        HttpStatus.BAD_REQUEST,
        IBaseErrorCode.ERROR_SUBMIT_DATA_INVALID_ENUM_VALUE,
        messageSourceUtils.getMessage(
            currentLocale.getLocale(),
            IBaseErrorCode.ERROR_SUBMIT_DATA_INVALID_ENUM_VALUE,
            ImmutableMap.of("error", exception.getMessage())));
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ResponseDTO<?> handleRuntimeException(RuntimeException exception) {
    exception.printStackTrace();
    return ResponseDTO.failure(
        HttpStatus.INTERNAL_SERVER_ERROR,
        IBaseErrorCode.ERROR_UNEXPECTED_ERROR,
        exception.getMessage());
  }

  @ExceptionHandler(WebClientResponseException.class)
  @ResponseBody
  public ResponseDTO<?> handleWebClientResponseException(
      WebClientResponseException exception, HttpServletResponse response) {
    try {
      ResponseDTO<?> responseBody = exception.getResponseBodyAs(ResponseDTO.class);
      response.setStatus(
          Optional.ofNullable(responseBody)
              .map(ResponseDTO::getStatus)
              .orElse(HttpStatus.INTERNAL_SERVER_ERROR.value()));
      return responseBody;
    } catch (Exception e) {
      response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
      return ResponseDTO.failure(
          HttpStatus.SERVICE_UNAVAILABLE,
          IBaseErrorCode.ERROR_SERVICE_UNAVAILABLE,
          exception.getMessage());
    }
  }

  @ExceptionHandler(WebClientRequestException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  @ResponseBody
  public ResponseDTO<?> handleWebClientRequestException(WebClientRequestException exception) {
    return ResponseDTO.failure(
        HttpStatus.SERVICE_UNAVAILABLE,
        IBaseErrorCode.ERROR_SERVICE_UNAVAILABLE,
        exception.getMessage());
  }

  @ExceptionHandler(FeignRetryableException.class)
  @ResponseBody
  public Mono<ResponseDTO<?>> handleFeignRetryableException(
      FeignRetryableException exception, HttpServletResponse response) {
    response.setStatus(exception.getStatus());
    return Mono.just(
        ResponseDTO.failure(
            HttpStatus.valueOf(exception.getStatus()),
            exception.getCode(),
            messageSourceUtils.getMessage(currentLocale.getLocale(), exception.getCode())));
  }

  private CommonException prepareErrorMessages(CommonException exception, WebRequest request) {
    Optional.ofNullable(exception.getCode())
        .ifPresent(
            errorCode -> {
              exception.setMessage(
                  messageSourceUtils.getMessageOrDefault(
                      currentLocale.getLocale(),
                      exception.getCode(),
                      BaseAppConstant.DEFAULT_ERROR_MESSAGE,
                      exception.getArgs()));
            });
    Optional.ofNullable(exception.getSubErrors())
        .ifPresent(
            subErrors ->
                subErrors.forEach(
                    error -> {
                      if (Objects.nonNull(error.getCode())) {
                        error.setMessage(
                            messageSourceUtils.getMessageOrDefault(
                                currentLocale.getLocale(),
                                error.getCode(),
                                BaseAppConstant.DEFAULT_ERROR_MESSAGE,
                                error.getArgs()));
                      }
                    }));
    return exception;
  }
}
