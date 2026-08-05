package com.teachermanagement.teacher_management.common.constant;

import java.util.List;

public class BaseAppConstant {
  public static final String DEFAULT_ERROR_MESSAGE = "Something went wrong.";
  public static final String DEFAULT_HEADER_TIMEZONE = "timezone";
  public static final String DEFAULT_SUCCESS_MESSAGE = "Request successfully.";
  public static final String COOKIE_KEY_ACCESS_TOKEN = "access_token";
  public static final String COOKIE_KEY_REFRESH_TOKEN = "refresh_token";
  public static final String COOKIE_KEY_SIGNED_IN = "signed_in";
  public static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer";
  public static final Integer DEFAULT_PAGE = 0;
  public static final Integer DEFAULT_PAGE_SIZE = 20;
  public static final String DEFAULT_TIMEZONE = "Etc/UTC";
  public static final String ANONYMOUS_USER = "anonymousUser";
  public static final int MASTER_DATA_ERROR_MESSAGE_LIMIT = 10;
  public static final String ROOT_PATH = "/";
  public static final String DEFAULT_LANGUAGE = "EN";
  public static final String DEFAULT_SIGNUP_ROLE = "USER";
  public static final int DEFAULT_DATA_SYNC_PAGE_SIZE = 500;
  public static final Integer MAX_IMPORT_DATA_LENGTH = 10000;
  public static final Integer DEFAULT_IMPORT_DATA_BATCH_SIZE = 500;
  public static final int DEFAULT_EXPORT_DATA_BATCH_SIZE = 500;
  public static final String REDIS_HASH_EXCECL_PROGRESS = "ExcelProgress";
  public static final String NAME_FIELD_TOTAL_HEADER_CONFIG = "totalHeaderConfig";
  public static final String NAME_FIELD_HEADER_CONFIGS = "headerConfigs";
  public static final String NAME_FIELD_EXPORT_PROCESSED = "exportProcessed";
  public static final String NAME_FIELD_ALL_RECORD_IDS = "allRecordIds";
  public static final String NAME_FIELD_TOTAL = "total";
  public static final List<String> LIST_VARIABLE_DEFAULT_FOR_EMAIL_TEMPLATE =
      List.of("sessionDomain", "sessionId", "lang");
}
