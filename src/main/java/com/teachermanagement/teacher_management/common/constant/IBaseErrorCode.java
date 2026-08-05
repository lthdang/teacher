package com.teachermanagement.teacher_management.common.constant;

import com.google.common.collect.ImmutableMap;
import com.teachermanagement.teacher_management.common.interfaces.ITranslationCode;

import java.util.Map;

public interface IBaseErrorCode extends ITranslationCode {
  String ERROR_SUBMIT_DATA_INVALID = "ERROR_SUBMIT_DATA_INVALID";
  String ERROR_USER_IS_NOT_AVAILABLE = "ERROR_USER_IS_NOT_AVAILABLE";
  String ERROR_USERNAME_PASSWORD_INVALID = "ERROR_USERNAME_PASSWORD_INVALID";
  String ERROR_SUBMIT_DATA_INVALID_ENUM_VALUE = "ERROR_SUBMIT_DATA_INVALID_ENUM_VALUE";
  String ERROR_UNEXPECTED_ERROR = "ERROR_UNEXPECTED_ERROR";
  String ERROR_SERVICE_UNAVAILABLE = "ERROR_SERVICE_UNAVAILABLE";
  String ERROR_PERMISSION_DENIED = "ERROR_PERMISSION_DENIED";
  String ERROR_JSON_PROCESSING = "ERROR_JSON_PROCESSING";
  String ERROR_UNAUTHORIZED = "ERROR_UNAUTHORIZED";
  String ERROR_EXCEL_HEADER_CONFIGS_NOT_FOUND = "ERROR_EXCEL_HEADER_CONFIGS_NOT_FOUND";
  String ERROR_EXCEL_SHEET_NOT_FOUND = "ERROR_EXCEL_SHEET_NOT_FOUND";
  String ERROR_EXCEL_SHEET_NOT_FOUND_BY_NAME = "ERROR_EXCEL_SHEET_NOT_FOUND_BY_NAME";
  String ERROR_EXCEL_HEADER_INVALID = "ERROR_EXCEL_HEADER_INVALID";
  String ERROR_EXCEL_CONFIG_HEADER_FIELD_NOT_FOUND = "ERROR_EXCEL_CONFIG_HEADER_FIELD_NOT_FOUND";
  String ERROR_EXCEL_CONFIG_HEADER_FIELD_TYPE_MISMATCH =
      "ERROR_EXCEL_CONFIG_HEADER_FIELD_TYPE_MISMATCH";
  String ERROR_EXCEL_CANNOT_CREATE_ROW_DATA_INSTANCE =
      "ERROR_EXCEL_CANNOT_CREATE_ROW_DATA_INSTANCE";
  String ERROR_NOT_FOUND_MASTER_DATA_TYPE = "ERROR_NOT_FOUND_MASTER_DATA_TYPE";
  String ERROR_MASTER_DATA_NOT_ENABLE_HISTORY_TRACKING =
      "ERROR_MASTER_DATA_NOT_ENABLE_HISTORY_TRACKING";
  String ERROR_SOME_MASTER_DATA_NOT_FOUND = "ERROR_SOME_MASTER_DATA_NOT_FOUND";
  String ERROR_MASTER_DATA_CODE_IS_REQUIRED = "ERROR_MASTER_DATA_CODE_IS_REQUIRED";
  String ERROR_MASTER_DATA_TYPE_IS_REQUIRED = "ERROR_MASTER_DATA_TYPE_IS_REQUIRED";
  String ERROR_MASTER_DATA_NAME_IS_REQUIRED = "ERROR_MASTER_DATA_NAME_IS_REQUIRED";
  String ERROR_NOT_FOUND_MASTER_DATA = "ERROR_NOT_FOUND_MASTER_DATA";
  String ERROR_MASTER_DATA_EXISTED = "ERROR_MASTER_DATA_EXISTED";
  String ERROR_MASTER_DATA_NOT_FOUND = "ERROR_MASTER_DATA_NOT_FOUND";
  String ERROR_MASTER_DATA_TYPE_NOT_EDITABLE = "ERROR_MASTER_DATA_TYPE_NOT_EDITABLE";
  String ERROR_COLUMN_PATH_IS_INVALID = "ERROR_COLUMN_PATH_IS_INVALID";
  String ERROR_INVALID_EXTRA_QUERY_KEY = "ERROR_INVALID_EXTRA_QUERY_KEY";
  String ERROR_INVALID_EXTRA_QUERY = "ERROR_INVALID_EXTRA_QUERY";
  String ERROR_DELETE_OBJECT_IDS_IS_REQUIRED = "ERROR_DELETE_OBJECT_IDS_IS_REQUIRED";
  String ERROR_DTO_MAPPER_PROCESSING = "ERROR_DTO_MAPPER_PROCESSING";
  String ERROR_INVALID_PASSWORD_FORMAT = "ERROR_INVALID_PASSWORD_FORMAT";
  String ERROR_MASTER_DATA_VALIDATE_FAILURE = "ERROR_MASTER_DATA_VALIDATE_FAILURE";
  String ERROR_SYSTEM_JOB_TASK_HAS_NOT_IMPLEMENTED = "ERROR_SYSTEM_JOB_TASK_HAS_NOT_IMPLEMENTED";
  String ERROR_MACHINE_TOKEN_CLIENT_NOT_FOUND = "ERROR_MACHINE_TOKEN_CLIENT_NOT_FOUND";
  String ERROR_MACHINE_TOKEN_SECRET_IS_INVALID = "ERROR_MACHINE_TOKEN_SECRET_IS_INVALID";
  String ERROR_NOT_FOUND_MICROSERVICE_HOST = "ERROR_NOT_FOUND_MICROSERVICE_HOST";
  String ERROR_ROLE_CODE_INVALID = "ERROR_ROLE_CODE_INVALID";
  String ERROR_ROLE_NAME_IS_REQUIRED = "ERROR_ROLE_NAME_IS_REQUIRED";
  String ERROR_ROLE_TYPE_IS_REQUIRED = "ERROR_ROLE_TYPE_IS_REQUIRED";
  String ERROR_ROLE_ID_IS_REQUIRED = "ERROR_ROLE_ID_IS_REQUIRED";
  String ERROR_LOAD_EXCEL_FILE_FAILURE = "ERROR_LOAD_EXCEL_FILE_FAILURE";
  String ERROR_LOAD_EXCEL_DATA_STREAM_FAILURE = "ERROR_LOAD_EXCEL_DATA_STREAM_FAILURE";
  String ERROR_SAVE_EXCEL_FILE_FAILURE = "ERROR_SAVE_EXCEL_FILE_FAILURE";
  String ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND";
  String ERROR_USER_NOT_FOUND_BY_USERNAME = "ERROR_USER_NOT_FOUND_BY_USERNAME";
  String ERROR_SOME_USERS_NOT_FOUND = "ERROR_SOME_USERS_NOT_FOUND";
  String ERROR_NOT_FOUND_ROLE_BY_ID = "ERROR_NOT_FOUND_ROLE_BY_ID";
  String ERROR_NOT_FOUND_SOME_ROLES_BY_ID = "ERROR_NOT_FOUND_SOME_ROLES_BY_ID";
  String ERROR_NOT_FOUND_NODE_BY_ID = "ERROR_NOT_FOUND_NODE_BY_ID";
  String ERROR_SOME_NODES_NOT_FOUND_BY_ID = "ERROR_SOME_NODES_NOT_FOUND_BY_ID";
  String ERROR_PERMISSION_NOT_FOUND_BY_ID = "ERROR_PERMISSION_NOT_FOUND_BY_ID";
  String ERROR_SOME_PERMISSIONS_NOT_FOUND_BY_ID = "ERROR_SOME_PERMISSIONS_NOT_FOUND_BY_ID";
  String ERROR_NOT_SUPPORTED_EXCEL_FILE = "ERROR_NOT_SUPPORTED_EXCEL_FILE";
  String ERROR_MASTER_DATA_VALIDATE_BY_TYPE_FAILURE = "ERROR_MASTER_DATA_VALIDATE_BY_TYPE_FAILURE";
  String ERROR_INVALID_MASTER_DATA_CODE = "ERROR_INVALID_MASTER_DATA_CODE";
  String ERROR_COULD_NOT_HASH_DATA = "ERROR_COULD_NOT_HASH_DATA";
  String ERROR_NOT_SUPPORTED_RESOURCE_EXTENSION = "ERROR_NOT_SUPPORTED_RESOURCE_EXTENSION";
  String ERROR_NOT_FOUND_RESOURCE_BY_ID = "ERROR_NOT_FOUND_RESOURCE_BY_ID";
  String ERROR_NOT_FOUND_SOME_RESOURCES_BY_ID = "ERROR_NOT_FOUND_SOME_RESOURCES_BY_ID";
  String ERROR_INVALID_DATA_TYPE = "ERROR_INVALID_DATA_TYPE";
  String ERROR_INVALID_EXPRESSION = "ERROR_INVALID_EXPRESSION";
  String ERROR_OPERAND_IS_NULL = "ERROR_OPERAND_IS_NULL";
  String ERROR_INVALID_CONSTANT_VALUE = "ERROR_INVALID_CONSTANT_VALUE";
  String ERROR_INVALID_CONSTANT_OR_UNIT_VALUE = "ERROR_INVALID_CONSTANT_OR_UNIT_VALUE";
  String ERROR_OPERATOR_IS_NOT_SUPPORTED = "ERROR_OPERATOR_IS_NOT_SUPPORTED";
  String ERROR_INVALID_NUMBER_FORMAT_IN_LIST = "ERROR_INVALID_NUMBER_FORMAT_IN_LIST";
  String ERROR_INVALID_DATE_RANGE = "ERROR_INVALID_DATE_RANGE";
  String ERROR_USERNAME_IS_REQUIRED = "ERROR_USERNAME_IS_REQUIRED";
  String ERROR_EMAIL_IS_REQUIRED = "ERROR_EMAIL_IS_REQUIRED";
  String ERROR_FIRSTNAME_IS_REQUIRED = "ERROR_FIRSTNAME_IS_REQUIRED";
  String ERROR_LASTNAME_IS_REQUIRED = "ERROR_LASTNAME_IS_REQUIRED";
  String ERROR_ROLES_IS_REQUIRED = "ERROR_ROLES_IS_REQUIRED";
  String ERROR_CONDITION_INPUT_DATATYPE_INVALID = "ERROR_CONDITION_INPUT_DATATYPE_INVALID";
  String ERROR_CONDITION_OPERATOR_INVALID = "ERROR_CONDITION_OPERATOR_INVALID";
  String ERROR_CONDITION_FUNCTION_NOT_SUPPORTED = "ERROR_CONDITION_FUNCTION_NOT_SUPPORTED";
  String ERROR_CONDITION_OPERAND_TYPE_IS_NULL = "ERROR_CONDITION_OPERAND_TYPE_IS_NULL";
  String ERROR_CANNOT_READ_VALUE_AS_CONDITION_SET_OPERAND =
      "ERROR_CANNOT_READ_VALUE_AS_CONDITION_SET_OPERAND";
  String ERROR_CANNOT_READ_VALUE_AS_CONDITION_OPERAND =
      "ERROR_CANNOT_READ_VALUE_AS_CONDITION_OPERAND";
  String ERROR_NOT_FOUND_CONDITION_PROPERTY = "ERROR_NOT_FOUND_CONDITION_PROPERTY";
  String ERROR_CONDITION_IS_INVALID = "ERROR_CONDITION_IS_INVALID";
  String ERROR_CONDITION_IS_REQUIRED = "ERROR_CONDITION_IS_REQUIRED";
  String ERROR_CANNOT_TOKENIZE_INVALID_CONDITION = "ERROR_CANNOT_TOKENIZE_INVALID_CONDITION";
  String ERROR_EXECUTE_OPERATOR = "ERROR_EXECUTE_OPERATOR";
  String ERROR_CONDITION_OPERATOR_IS_NULL = "ERROR_CONDITION_OPERATOR_IS_NULL";
  String ERROR_RESOURCE_ID_FIELD_NOT_FOUND = "ERROR_RESOURCE_ID_FIELD_NOT_FOUND";
  String ERROR_RESOURCE_FIELD_NOT_FOUND = "ERROR_RESOURCE_FIELD_NOT_FOUND";
  String ERROR_NOT_FOUND_TRANSLATION_BY_ID = "ERROR_NOT_FOUND_TRANSLATION_BY_ID";
  String ERROR_NOT_FOUND_SOME_TRANSLATIONS_BY_IDS = "ERROR_NOT_FOUND_SOME_TRANSLATIONS_BY_IDS";

  String ERROR_VARIABLE_WRONG = "ERROR_VARIABLE_WRONG";
  String ERROR_PASSWORD_IS_REQUIRED = "ERROR_PASSWORD_IS_REQUIRED";
  String ERROR_CLIENT_IS_REQUIRED = "ERROR_CLIENT_IS_REQUIRED";
  String ERROR_SECRET_IS_REQUIRED = "ERROR_SECRET_IS_REQUIRED";
  String ERROR_REFRESH_TOKEN_IS_REQUIRED = "ERROR_REFRESH_TOKEN_IS_REQUIRED";
  String ERROR_MACHINE_TOKEN_CLIENT_IS_REQUIRED = "ERROR_MACHINE_TOKEN_CLIENT_IS_REQUIRED";
  String ERROR_MACHINE_TOKEN_SECRET_IS_REQUIRED = "ERROR_MACHINE_TOKEN_SECRET_IS_REQUIRED";
  String ERROR_REFRESH_TOKEN_EXPIRED = "ERROR_REFRESH_TOKEN_EXPIRED";
  String ERROR_REFRESH_TOKEN_INVALID = "ERROR_REFRESH_TOKEN_INVALID";
  String ERROR_JWT_TOKEN_INVALID = "ERROR_JWT_TOKEN_INVALID";
  String ERROR_PERMISSION_NAME_IS_REQUIRED = "ERROR_PERMISSION_NAME_IS_REQUIRED";
  String ERROR_EMAIL_EXISTED = "ERROR_EMAIL_EXISTED";
  String ERROR_USERNAME_EXISTED = "ERROR_USERNAME_EXISTED";
  String ERROR_NOT_FOUND_API_ACCESS_TOKEN_BY_ID = "ERROR_NOT_FOUND_API_ACCESS_TOKEN_BY_ID";
  String ERROR_NOT_FOUND_SOME_API_ACCESS_TOKENS_BY_IDS =
      "ERROR_NOT_FOUND_SOME_API_ACCESS_TOKENS_BY_IDS";
  String ERROR_EXPIRED_API_ACCESS_TOKEN = "ERROR_EXPIRED_API_ACCESS_TOKEN";
  String ERROR_INVALID_API_ACCESS_TOKEN = "ERROR_INVALID_API_ACCESS_TOKEN";
  String ERROR_NOT_FOUND_RESOURCE_PATH = "ERROR_NOT_FOUND_RESOURCE_PATH";
  String ERROR_ACCESS_TOKEN_NAME_IS_REQUIRED = "ERROR_ACCESS_TOKEN_NAME_IS_REQUIRED";
  String ERROR_EXPIRED_DATE_IS_REQUIRED = "ERROR_EXPIRED_DATE_IS_REQUIRED";
  String ERROR_NOT_FOUND_UPLOAD_PROGRESS = "ERROR_NOT_FOUND_UPLOAD_PROGRESS";
  String ERROR_EXCEL_EXCEED_LIMIT_ALLOWED = "ERROR_EXCEL_EXCEED_LIMIT_ALLOWED";
  String ERROR_UPLOAD_PROGRESS_EXISTED = "ERROR_UPLOAD_PROGRESS_EXISTED";
  String ERROR_UPLOAD_PROGRESS_HAS_NOT_COMPLETED_YET =
      "ERROR_UPLOAD_PROGRESS_HAS_NOT_COMPLETED_YET";
  String ERROR_NOT_FOUND_ROW_DATA_FROM_CACHE = "ERROR_NOT_FOUND_ROW_DATA_FROM_CACHE";
  static final Map<String, String> messages =
      ImmutableMap.<String, String>builder()
          .put(ERROR_UNAUTHORIZED, "Unauthorized error")
          .put(ERROR_UNEXPECTED_ERROR, "Unexpected server error")
          .put(ERROR_SERVICE_UNAVAILABLE, "Service temporarily unavailable")
          .put(
              ERROR_SYSTEM_JOB_TASK_HAS_NOT_IMPLEMENTED,
              "System job task '{{task}}' has not been implemented yet")
          .put(ERROR_INVALID_EXTRA_QUERY_KEY, "Invalid extra query key: {{key}}")
          .put(
              ERROR_INVALID_EXTRA_QUERY,
              "Invalid extra query '{{value}}', extra query must have result includes root id belong to result data (root.id, expectedData)")
          .put(ERROR_USERNAME_PASSWORD_INVALID, "Username or password is invalid, please try again")
          .put(ERROR_USER_IS_NOT_AVAILABLE, "User is not available")
          .put(ERROR_SUBMIT_DATA_INVALID, "Submitted data is invalid")
          .put(ERROR_INVALID_PASSWORD_FORMAT, "Invalid password format")
          .put(ERROR_NOT_FOUND_MASTER_DATA_TYPE, "Could not be found master data type: {{type}}")
          .put(ERROR_MASTER_DATA_CODE_IS_REQUIRED, "Master data code is required")
          .put(ERROR_MASTER_DATA_NAME_IS_REQUIRED, "Master data name is required")
          .put(ERROR_MASTER_DATA_TYPE_IS_REQUIRED, "Master data type is required")
          .put(ERROR_NOT_FOUND_MASTER_DATA, "Not found master data with code: {{code}}")
          .put(ERROR_COLUMN_PATH_IS_INVALID, "Column path is invalid: {{path}}")
          .put(ERROR_SUBMIT_DATA_INVALID_ENUM_VALUE, "Enum value is invalid: {{value}}")
          .put(ERROR_PERMISSION_DENIED, "You don't have permission to access this resource")
          .put(ERROR_DELETE_OBJECT_IDS_IS_REQUIRED, "Delete object ids is required")
          .put(ERROR_JSON_PROCESSING, "Unexpected error while processing JSON object")
          .put(ERROR_MASTER_DATA_EXISTED, "This master data code has already existed: {{code}}")
          .put(
              ERROR_MASTER_DATA_NOT_ENABLE_HISTORY_TRACKING,
              "This master data type not supported history tracking data: {{type}}")
          .put(
              ERROR_SOME_MASTER_DATA_NOT_FOUND,
              "Some master data could not be found by code: {{codes}}")
          .put(ERROR_MASTER_DATA_NOT_FOUND, "Master data could not be found by code: {{code}}")
          .put(
              ERROR_MASTER_DATA_TYPE_NOT_EDITABLE,
              "This master data belongs to a non-editable master data type: {{type}}")
          .put(
              ERROR_DTO_MAPPER_PROCESSING,
              "Unexpected error while processing mapping dto from entity: {{error}}")
          .put(
              ERROR_MASTER_DATA_VALIDATE_FAILURE,
              "The master data is invalid, master data for {{type}} must be one of the following: [{{availableValues}}]")
          .put(ERROR_EXCEL_HEADER_CONFIGS_NOT_FOUND, "Excel header configs not found")
          .put(
              ERROR_EXCEL_SHEET_NOT_FOUND,
              "Excel sheet not found, please use a sheet before reading data")
          .put(ERROR_EXCEL_SHEET_NOT_FOUND_BY_NAME, "Excel sheet not found by name: {{sheetName}}")
          .put(
              ERROR_EXCEL_HEADER_INVALID,
              "Excel header is invalid, read value {{header}} but expected {{expectedHeader}}")
          .put(
              ERROR_EXCEL_CONFIG_HEADER_FIELD_NOT_FOUND,
              "Excel header config field not found: {{fieldName}}")
          .put(
              ERROR_EXCEL_CONFIG_HEADER_FIELD_TYPE_MISMATCH,
              "Excel header config field type mismatch, expected type {{type}} for field {{fieldName}}")
          .put(
              ERROR_EXCEL_CANNOT_CREATE_ROW_DATA_INSTANCE,
              "Error when reading Excel file, cannot create row data instance of type {{type}}")
          .put(
              ERROR_MACHINE_TOKEN_CLIENT_NOT_FOUND,
              "Machine token client could not be found: {{client}}")
          .put(ERROR_NOT_FOUND_MICROSERVICE_HOST, "Not found service host for client: {{client}}")
          .put(ERROR_LOAD_EXCEL_FILE_FAILURE, "Fail to read Excel file: {{fileName}}")
          .put(ERROR_LOAD_EXCEL_DATA_STREAM_FAILURE, "Fail to read Excel data from input stream")
          .put(ERROR_SAVE_EXCEL_FILE_FAILURE, "Fail to save Excel file")
          .put(ERROR_USER_NOT_FOUND, "User could not be found by id: {{id}}")
          .put(ERROR_SOME_USERS_NOT_FOUND, "Some users could not be found by ids: {{ids}}")
          .put(ERROR_NOT_FOUND_ROLE_BY_ID, "Role could not be found by id: {{id}}")
          .put(ERROR_NOT_FOUND_NODE_BY_ID, "Node could not be found by id: {{id}}")
          .put(ERROR_SOME_NODES_NOT_FOUND_BY_ID, "Some nodes could not be found by ids: {{ids}}")
          .put(ERROR_PERMISSION_NOT_FOUND_BY_ID, "Permission could not be found by id: {{id}}")
          .put(
              ERROR_SOME_PERMISSIONS_NOT_FOUND_BY_ID,
              "Some of the permissions could not be found by ids: {{ids}}")
          .put(
              ERROR_USER_NOT_FOUND_BY_USERNAME, "User could not be found by username: {{username}}")
          .put(ERROR_COULD_NOT_HASH_DATA, "Unexpected error occurs when hashing data: {{error}}")
          .put(
              ERROR_NOT_SUPPORTED_RESOURCE_EXTENSION,
              "Resource extension has not been supported: {{extension}}")
          .put(
              ERROR_NOT_FOUND_SOME_RESOURCES_BY_ID,
              "Some resources could not be found by ids: {{ids}}")
          .put(ERROR_NOT_FOUND_RESOURCE_BY_ID, "Resource could not be found by id: {{id}}")
          .put(
              ERROR_CONDITION_OPERATOR_INVALID,
              "Condition operator is not supported: '{{operator}}', please give one of the following [{{availableOperators}}]")
          .put(ERROR_CONDITION_OPERATOR_IS_NULL, "Condition operator is null")
          .put(
              ERROR_CONDITION_INPUT_DATATYPE_INVALID,
              "Condition input datatype is not supported: {{type}}, please give one of the following [{{availableTypes}}]")
          .put(
              ERROR_CONDITION_IS_INVALID,
              "Condition is invalid '{{condition}}', make sure condition has only one supported operator, operands' name is in camel case, string constants are between single quotes and between operator and operands has only one space character")
          .put(
              ERROR_CANNOT_READ_VALUE_AS_CONDITION_OPERAND,
              "Validate condition failed, could not read value '{{value}}' condition operand type {{type}}")
          .put(
              ERROR_CANNOT_READ_VALUE_AS_CONDITION_SET_OPERAND,
              "Validate condition failed, could not read set values from '{{values}}' as operand type {{type}} elements")
          .put(
              ERROR_CONDITION_OPERAND_TYPE_IS_NULL,
              "The type specified for the operand '{{type}}' is null")
          .put(
              ERROR_NOT_FOUND_CONDITION_PROPERTY,
              "Property '{{propName}}' could not be found from condition input datatype")
          .put(
              ERROR_EXECUTE_OPERATOR,
              "Could not execute operator '{{operator}}' on 2 different data type operands: '{{value1}}' ({{type1}}), '{{value2}}' ({{type2}})")
          .put(
              ERROR_OPERATOR_IS_NOT_SUPPORTED,
              "Condition operator '{{operator}}' is not supported on data type '{{type}}'")
          .put(
              ERROR_CANNOT_TOKENIZE_INVALID_CONDITION,
              "Cannot tokenize condition '{{condition}}', please make sure the condition is in valid format")
          .put(
              ERROR_CONDITION_FUNCTION_NOT_SUPPORTED,
              "Condition function '{{function}}' is not supported")
          .put(ERROR_CONDITION_IS_REQUIRED, "Condition is required")
          .put(ERROR_ROLE_CODE_INVALID, "Role code is invalid")
          .put(ERROR_ROLE_NAME_IS_REQUIRED, "Role name is required")
          .put(ERROR_ROLE_TYPE_IS_REQUIRED, "Role type is required")
          .put(ERROR_ROLE_ID_IS_REQUIRED, "Role ID is required")
          .put(ERROR_NOT_FOUND_SOME_ROLES_BY_ID, "Some roles could not be found by ids: {{ids}}")
          .put(ERROR_INVALID_MASTER_DATA_CODE, "Invalid master data code: {{code}}")
          .put(ERROR_INVALID_DATA_TYPE, "Invalid data type: {{type}}")
          .put(ERROR_INVALID_EXPRESSION, "Invalid expression: {{expression}}")
          .put(ERROR_OPERAND_IS_NULL, "Operand is null")
          .put(ERROR_INVALID_CONSTANT_VALUE, "Invalid constant value: {{value}}")
          .put(ERROR_INVALID_CONSTANT_OR_UNIT_VALUE, "Invalid constant or unit value: {{value}}")
          .put(ERROR_INVALID_NUMBER_FORMAT_IN_LIST, "Invalid number format in list: {{value}}")
          .put(ERROR_INVALID_DATE_RANGE, "Invalid date range: {{start}}-{{end}}")
          .put(ERROR_USERNAME_IS_REQUIRED, "Username is required")
          .put(ERROR_EMAIL_IS_REQUIRED, "Email is required")
          .put(ERROR_FIRSTNAME_IS_REQUIRED, "First name is required")
          .put(ERROR_LASTNAME_IS_REQUIRED, "Last name is required")
          .put(ERROR_ROLES_IS_REQUIRED, "Roles are required")
          .put(ERROR_RESOURCE_ID_FIELD_NOT_FOUND, "Resource ID field not found: {{fieldName}}")
          .put(ERROR_RESOURCE_FIELD_NOT_FOUND, "Resource field not found: {{fieldName}}")
          .put(ERROR_NOT_FOUND_TRANSLATION_BY_ID, "Translation could not be found by id: {{id}}")
          .put(
              ERROR_NOT_FOUND_SOME_TRANSLATIONS_BY_IDS,
              "Some translations could not be found by ids: {{ids}}")
          .put(
              ERROR_NOT_SUPPORTED_EXCEL_FILE,
              "The provided Excel file format is not supported: {{fileName}}")
          .put(
              ERROR_MASTER_DATA_VALIDATE_BY_TYPE_FAILURE,
              "The master data {{value}} is invalid, master data for {{type}} must be one of the following: [{{availableValues}}]")
          .put(ERROR_VARIABLE_WRONG, "Variable {{variable}} not found in input data ")
          .put(ERROR_PASSWORD_IS_REQUIRED, "Password is required")
          .put(ERROR_CLIENT_IS_REQUIRED, "Client is required")
          .put(ERROR_SECRET_IS_REQUIRED, "Secret is required")
          .put(ERROR_REFRESH_TOKEN_IS_REQUIRED, "Refresh token is required")
          .put(ERROR_REFRESH_TOKEN_EXPIRED, "Refresh token is expired")
          .put(ERROR_REFRESH_TOKEN_INVALID, "Refresh token is invalid")
          .put(ERROR_MACHINE_TOKEN_CLIENT_IS_REQUIRED, "Machine token client is required")
          .put(ERROR_MACHINE_TOKEN_SECRET_IS_REQUIRED, "Machine token secret client is required")
          .put(ERROR_JWT_TOKEN_INVALID, "JWT token is invalid or expired")
          .put(ERROR_PERMISSION_NAME_IS_REQUIRED, "Permission name is required")
          .put(ERROR_EMAIL_EXISTED, "Email already exists")
          .put(ERROR_USERNAME_EXISTED, "Username already exists")
          .put(
              ERROR_NOT_FOUND_API_ACCESS_TOKEN_BY_ID,
              "API access token could not be found by id: {{id}}")
          .put(
              ERROR_NOT_FOUND_SOME_API_ACCESS_TOKENS_BY_IDS,
              "Some API access tokens could not be found by ids: {{ids}}")
          .put(ERROR_EXPIRED_API_ACCESS_TOKEN, "API access token is expired: {{token}}")
          .put(ERROR_INVALID_API_ACCESS_TOKEN, "Invalid API access token: {{token}}")
          .put(ERROR_NOT_FOUND_RESOURCE_PATH, "Resource not found: {{resourcePath}}")
          .put(ERROR_ACCESS_TOKEN_NAME_IS_REQUIRED, "Access token name is required")
          .put(ERROR_EXPIRED_DATE_IS_REQUIRED, "Expired date is required")
          .put(ERROR_NOT_FOUND_UPLOAD_PROGRESS, "Upload progress could not be found")
          .put(ERROR_EXCEL_EXCEED_LIMIT_ALLOWED, "MOF result data exceeded limit allow > {{limit}}")
          .put(
              ERROR_UPLOAD_PROGRESS_EXISTED,
              "There was an existing upload progress, please complete or cancel it before start a new one.")
          .put(
              ERROR_UPLOAD_PROGRESS_HAS_NOT_COMPLETED_YET,
              "Upload progress has not been completed yet")
          .put(ERROR_NOT_FOUND_ROW_DATA_FROM_CACHE, "Data not found in cache with key is: {key}")
          .build();
}
