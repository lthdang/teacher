package com.teachermanagement.teacher_management.common.exception;

import java.util.Locale;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;

import com.teachermanagement.teacher_management.common.constant.BaseAppConstant;
import com.teachermanagement.teacher_management.common.constant.IBaseErrorCode;
import com.teachermanagement.teacher_management.common.dto.ResponseDTO;
import com.teachermanagement.teacher_management.common.model.LocaleInformation;
import com.teachermanagement.teacher_management.common.util.JsonUtils;
import com.teachermanagement.teacher_management.common.util.MessageSourceUtils;

import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseUnauthorizedHandler implements ServerAuthenticationEntryPoint {
  private final MessageSourceUtils messageSourceUtils;

  //  @Override
  //  public void commence(
  //      HttpServletRequest request,
  //      HttpServletResponse response,
  //      AuthenticationException authException)
  //      throws IOException {
  //    log.error("Unauthorized error: {}", authException.getMessage());
  //    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
  //    response.setStatus(HttpStatus.UNAUTHORIZED.value());
  //    ResponseDTO<?> responseData =
  //        ResponseDTO.failure(
  //            HttpStatus.UNAUTHORIZED,
  //            IBaseErrorCode.ERROR_UNAUTHORIZED,
  //            messageSourceUtils.getMessage(
  //                currentLocale.getLocale(), IBaseErrorCode.ERROR_UNAUTHORIZED));
  //    objectMapper.writeValue(response.getOutputStream(), responseData);
  //  }

  private LocaleInformation getLocaleInformation(ServerHttpRequest request) {
    TimeZone timezone =
        request.getHeaders().getOrEmpty(BaseAppConstant.DEFAULT_HEADER_TIMEZONE).stream()
            .findFirst()
            .map(TimeZone::getTimeZone)
            .orElse(null);
    Locale locale =
        request.getHeaders().getOrEmpty(HttpHeaders.ACCEPT_LANGUAGE).stream()
            .findFirst()
            .map(Locale::forLanguageTag)
            .orElse(Locale.getDefault());
    return LocaleInformation.builder().timezone(timezone).locale(locale).build();
  }

  @Override
  public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException authException) {
    log.error("Unauthorized error: {}", authException.getMessage());
    ServerHttpResponse response = exchange.getResponse();
    LocaleInformation currentLocale = getLocaleInformation(exchange.getRequest());
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    ResponseDTO<?> responseData =
        ResponseDTO.failure(
            HttpStatus.UNAUTHORIZED,
            IBaseErrorCode.ERROR_UNAUTHORIZED,
            messageSourceUtils.getMessage(
                currentLocale.getLocale(), IBaseErrorCode.ERROR_UNAUTHORIZED));
    DataBufferFactory bufferFactory = response.bufferFactory();
    DataBuffer buffer = bufferFactory.wrap(JsonUtils.toJsonString(responseData).getBytes());
    return response.writeWith(Mono.just(buffer));
  }
}
