package com.teachermanagement.teacher_management.common.constant;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ErrorCode implements IBaseErrorCode {
  public static final String ERROR_USERNAME_IS_REQUIRED = "ERROR_USERNAME_IS_REQUIRED";
  public static final String ERROR_PASSWORD_IS_REQUIRED = "ERROR_PASSWORD_IS_REQUIRED";
  public static final String ERROR_PASSWORD_INCORRECT = "ERROR_PASSWORD_INCORRECT";
  public static final String ERROR_EMAIL_IS_REQUIRED = "ERROR_EMAIL_IS_REQUIRED";
  public static final String ERROR_FIRSTNAME_IS_REQUIRED = "ERROR_FIRSTNAME_IS_REQUIRED";
  public static final String ERROR_LASTNAME_IS_REQUIRED = "ERROR_LASTNAME_IS_REQUIRED";
  public static final String ERROR_USERNAME_EXISTED = "ERROR_USERNAME_EXISTED";
  public static final String ERROR_EMAIL_EXISTED = "ERROR_EMAIL_EXISTED";

  private static final Map<String, String> messages =
      ImmutableMap.<String, String>builder()
          .put(ERROR_USERNAME_IS_REQUIRED, "Username is required")
          .put(ERROR_PASSWORD_IS_REQUIRED, "Password is required")
          .put(ERROR_PASSWORD_INCORRECT, "Password is incorrect")
          .put(ERROR_EMAIL_IS_REQUIRED, "Email is required")
          .put(ERROR_FIRSTNAME_IS_REQUIRED, "First name is required")
          .put(ERROR_LASTNAME_IS_REQUIRED, "Last name is required")
          .put(ERROR_USERNAME_EXISTED, "Username already exists")
          .put(ERROR_EMAIL_EXISTED, "Email already exists")
          .put(ERROR_REFRESH_TOKEN_IS_REQUIRED, "Refresh token is required")
          .put(ERROR_REFRESH_TOKEN_EXPIRED, "Refresh token is expired")
          .put(ERROR_REFRESH_TOKEN_INVALID, "Refresh token is invalid")
          .build();

  @Override
  public Map<String, String> getBaseMessages() {
    return IBaseErrorCode.messages;
  }

  @Override
  public Map<String, String> getMessages() {
    return messages;
  }
}
