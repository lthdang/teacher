package com.teacher.common.exception;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.teacher.common.constant.BaseAppConstant;
import com.teacher.common.constant.IBaseErrorCode;
import com.teacher.common.dto.ResponseDTO;
import com.teacher.common.exception.CommonException.SubError;
import com.teacher.common.model.LocaleInformation;
import com.teacher.common.util.MessageSourceUtils;

import java.util.Locale;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import com.teacher.common.interfaces.ITranslationCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private final MessageSourceUtils messageSourceUtils;
  private final LocaleInformation currentLocale;
  private final ITranslationCode translationCode;

  @Autowired
  public GlobalExceptionHandler(
      @Autowired(required = false) MessageSourceUtils messageSourceUtils,
      @Autowired(required = false) LocaleInformation currentLocale,
      @Autowired(required = false) ITranslationCode translationCode) {
    this.messageSourceUtils = messageSourceUtils;
    this.currentLocale = currentLocale;
    this.translationCode = translationCode;
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ResponseDTO<?>> handleBadRequestException(
      BadRequestException ex, HttpServletRequest request) {
    return respond(HttpStatus.BAD_REQUEST, resolveException(ex), request);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ResponseDTO<?>> handleNotFoundException(
      NotFoundException ex, HttpServletRequest request) {
    return respond(HttpStatus.NOT_FOUND, resolveException(ex), request);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ResponseDTO<?>> handleForbiddenException(
      ForbiddenException ex, HttpServletRequest request) {
    return respond(HttpStatus.FORBIDDEN, resolveException(ex), request);
  }

  @ExceptionHandler(UnexpectedException.class)
  public ResponseEntity<ResponseDTO<?>> handleUnexpectedException(
      UnexpectedException ex, HttpServletRequest request) {
    return respond(HttpStatus.INTERNAL_SERVER_ERROR, resolveException(ex), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ResponseDTO<?>> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    List<SubError> subErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> SubError.with(fe.getField() + ": " + fe.getDefaultMessage()))
            .toList();
    return respondWithSubErrors(
        HttpStatus.BAD_REQUEST, IBaseErrorCode.ERROR_SUBMIT_DATA_INVALID, subErrors, request);
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ResponseDTO<?>> handleBindException(
      BindException ex, HttpServletRequest request) {
    List<SubError> subErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> SubError.with(fe.getField() + ": " + fe.getDefaultMessage()))
            .toList();
    return respondWithSubErrors(
        HttpStatus.BAD_REQUEST, IBaseErrorCode.ERROR_SUBMIT_DATA_INVALID, subErrors, request);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ResponseDTO<?>> handleValidationException(
      ValidationException ex, HttpServletRequest request) {
    if (ex.getCause() instanceof CommonException commonEx) {
      return respond(HttpStatus.BAD_REQUEST, resolveException(commonEx), request);
    }
    return respondWithCode(
        HttpStatus.BAD_REQUEST, IBaseErrorCode.ERROR_SUBMIT_DATA_INVALID, null, request);
  }

  @ExceptionHandler(DisabledException.class)
  public ResponseEntity<ResponseDTO<?>> handleDisabledException(
      DisabledException ex, HttpServletRequest request) {
    return respondWithCode(
        HttpStatus.BAD_REQUEST, IBaseErrorCode.ERROR_USER_IS_NOT_AVAILABLE, null, request);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ResponseDTO<?>> handleBadCredentialsException(
      BadCredentialsException ex, HttpServletRequest request) {
    return respondWithCode(
        HttpStatus.BAD_REQUEST, IBaseErrorCode.ERROR_USERNAME_PASSWORD_INVALID, null, request);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ResponseDTO<?>> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    return respondWithCode(
        HttpStatus.BAD_REQUEST,
        IBaseErrorCode.ERROR_SUBMIT_DATA_INVALID_ENUM_VALUE,
        Map.of("error", String.valueOf(ex.getMessage())),
        request);
  }

  @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
  public ResponseEntity<ResponseDTO<?>> handleEndpointNotFound(
      Exception ex, HttpServletRequest request) {
    String message = "Endpoint '" + request.getRequestURI() + "' does not exist in the system";
    log.warn("{} -> 404 {}", request.getRequestURI(), message);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ResponseDTO.failure(HttpStatus.NOT_FOUND, null, message));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ResponseDTO<?>> handleIllegalState(
      IllegalStateException ex, HttpServletRequest request) {
    log.warn("{} -> 404 {}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ResponseDTO.failure(HttpStatus.NOT_FOUND, null, ex.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ResponseDTO<?>> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request) {
    log.warn("{} -> 400 {}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ResponseDTO.failure(HttpStatus.BAD_REQUEST, null, ex.getMessage()));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ResponseDTO<?>> handleRuntimeException(
      RuntimeException ex, HttpServletRequest request) {
    log.error("Unhandled RuntimeException at {}", request.getRequestURI(), ex);
    return respondWithCode(
        HttpStatus.INTERNAL_SERVER_ERROR, IBaseErrorCode.ERROR_UNEXPECTED_ERROR, null, request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResponseDTO<?>> handleGeneralException(
      Exception ex, HttpServletRequest request) {
    log.error("Unhandled Exception at {}", request.getRequestURI(), ex);
    return respondWithCode(
        HttpStatus.INTERNAL_SERVER_ERROR, IBaseErrorCode.ERROR_UNEXPECTED_ERROR, null, request);
  }

  // ---------- helpers ----------

  private CommonException resolveException(CommonException ex) {
    ex.setMessage(resolve(ex.getCode(), ex.getArgs()));
    if (ex.getSubErrors() != null) {
      ex.getSubErrors()
          .forEach(
              sub -> {
                if (sub.getCode() != null) {
                  sub.setMessage(resolve(sub.getCode(), sub.getArgs()));
                }
              });
    }
    return ex;
  }

  private String resolve(String code, Map<String, Object> args) {
    if (code == null) return BaseAppConstant.DEFAULT_ERROR_MESSAGE;

    String defaultFallback = BaseAppConstant.DEFAULT_ERROR_MESSAGE;
    if (translationCode != null) {
      String codeMessage = translationCode.getMessage(code);
      if (codeMessage != null && !Objects.equals(codeMessage, code)) {
        defaultFallback = codeMessage;
      }
    }

    if (messageSourceUtils != null) {
      Locale locale = (currentLocale != null && currentLocale.getLocale() != null)
          ? currentLocale.getLocale()
          : Locale.getDefault();
      return messageSourceUtils.getMessageOrDefault(
          locale, code, defaultFallback, args);
    }
    return defaultFallback;
  }

  private ResponseEntity<ResponseDTO<?>> respond(
      HttpStatus status, CommonException ex, HttpServletRequest request) {
    log.warn("{} -> {} {}", request.getRequestURI(), status.value(), ex.getCode());
    return ResponseEntity.status(status).body(ResponseDTO.failure(status, ex));
  }

  private ResponseEntity<ResponseDTO<?>> respondWithCode(
      HttpStatus status, String code, Map<String, Object> args, HttpServletRequest request) {
    String message = resolve(code, args);
    log.warn("{} -> {} {}", request.getRequestURI(), status.value(), code);
    return ResponseEntity.status(status).body(ResponseDTO.failure(status, code, message));
  }

  private ResponseEntity<ResponseDTO<?>> respondWithSubErrors(
      HttpStatus status, String code, List<SubError> subErrors, HttpServletRequest request) {
    String message = resolve(code, null);
    log.warn("{} -> {} {}", request.getRequestURI(), status.value(), code);
    return ResponseEntity.status(status)
        .body(ResponseDTO.failure(status, code, message, subErrors));
  }
}