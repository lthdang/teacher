package com.teacher.common.constant;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ErrorCode implements IBaseErrorCode {
  // IS_REQUIRED
  public static final String ERROR_USERNAME_IS_REQUIRED = "ERROR_USERNAME_IS_REQUIRED";
  public static final String ERROR_PASSWORD_IS_REQUIRED = "ERROR_PASSWORD_IS_REQUIRED";
  public static final String ERROR_FIRSTNAME_IS_REQUIRED = "ERROR_FIRSTNAME_IS_REQUIRED";
  public static final String ERROR_LASTNAME_IS_REQUIRED = "ERROR_LASTNAME_IS_REQUIRED";
  public static final String ERROR_EMAIL_IS_REQUIRED = "ERROR_EMAIL_IS_REQUIRED";
  // INCORRECT
  public static final String ERROR_PASSWORD_INCORRECT = "ERROR_PASSWORD_INCORRECT";
  // EXISTED
  public static final String ERROR_USERNAME_EXISTED = "ERROR_USERNAME_EXISTED";
  public static final String ERROR_EMAIL_EXISTED = "ERROR_EMAIL_EXISTED";
  public static final String ERROR_ROLE_CODE_EXISTED = "ERROR_ROLE_CODE_EXISTED";
  public static final String ERROR_CANNOT_DELETE_SYSTEM_ROLE = "ERROR_CANNOT_DELETE_SYSTEM_ROLE";
  public static final String ERROR_ROLE_IN_USE = "ERROR_ROLE_IN_USE";
  // TENANT
  public static final String ERROR_NOT_FOUND_TENANT_BY_ID = "ERROR_NOT_FOUND_TENANT_BY_ID";
  public static final String ERROR_NOT_FOUND_SOME_TENANTS_BY_ID = "ERROR_NOT_FOUND_SOME_TENANTS_BY_ID";
  public static final String ERROR_TENANT_SLUG_EXISTED = "ERROR_TENANT_SLUG_EXISTED";
  public static final String ERROR_TENANT_IN_USE = "ERROR_TENANT_IN_USE";

  // USER
  public static final String ERROR_NOT_FOUND_USER_BY_ID = "ERROR_NOT_FOUND_USER_BY_ID";
  public static final String ERROR_NOT_FOUND_SOME_USERS_BY_ID = "ERROR_NOT_FOUND_SOME_USERS_BY_ID";

  private static final Map<String, String> messages =
      ImmutableMap.<String, String>builder()
          .put(ERROR_USERNAME_IS_REQUIRED, "Username is required")
          .put(ERROR_PASSWORD_IS_REQUIRED, "Password is required")
          .put(ERROR_EMAIL_IS_REQUIRED, "Email is required")
          .put(ERROR_FIRSTNAME_IS_REQUIRED, "First name is required")
          .put(ERROR_LASTNAME_IS_REQUIRED, "Last name is required")
          .put(ERROR_PASSWORD_INCORRECT, "Password is incorrect")
          .put(ERROR_USERNAME_EXISTED, "Username already exists")
          .put(ERROR_EMAIL_EXISTED, "Email already exists")
          .put(ERROR_ROLE_CODE_EXISTED, "Role code already exists")
          .put(ERROR_CANNOT_DELETE_SYSTEM_ROLE, "System roles cannot be deleted")
          .put(ERROR_ROLE_IN_USE, "Cannot delete role because it is currently assigned to users")
          .put(ERROR_NOT_FOUND_TENANT_BY_ID, "Tenant could not be found by id: {{id}}")
          .put(ERROR_NOT_FOUND_SOME_TENANTS_BY_ID, "Some tenants could not be found by ids: {{ids}}")
          .put(ERROR_TENANT_SLUG_EXISTED, "Tenant slug already exists")
          .put(ERROR_TENANT_IN_USE, "Cannot delete tenant because it is currently assigned to users or departments")
          .put(ERROR_NOT_FOUND_USER_BY_ID, "User could not be found by id: {{id}}")
          .put(ERROR_NOT_FOUND_SOME_USERS_BY_ID, "Some users could not be found by ids: {{ids}}")
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
