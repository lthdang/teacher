package com.teachermanagement.teacher_management.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ReflectionUtils {
  public static List<Field> getAllFieldsFromObject(Object object) {
    return getAllFieldsFromClass(object.getClass());
  }

  public static List<Field> getAllFieldsFromClass(Class<?> type) {
    List<Field> allOutputFields = new ArrayList<>(Arrays.asList(type.getDeclaredFields()));
    allOutputFields.addAll(
        Optional.ofNullable(type.getSuperclass())
            .filter(
                superClass ->
                    Stream.of("BaseEntity", "DTO", "Item")
                        .anyMatch(suffix -> superClass.getName().endsWith(suffix)))
            .map(ReflectionUtils::getAllFieldsFromClass)
            .orElse(Collections.emptyList()));
    return allOutputFields;
  }

  public static Optional<Field> getFieldFromObject(Object object, String fieldName) {
    return getFieldFromClass(object.getClass(), fieldName);
  }

  public static Optional<Field> getFieldFromClass(Class<?> type, String fieldName) {
    return getAllFieldsFromClass(type).stream()
        .filter(field -> field.getName().equals(fieldName))
        .findFirst();
  }

  /**
   * Get string value from object with given field
   *
   * @param field target field
   * @param object target object
   * @return String
   */
  public static String getStringFromObject(Object object, Field field) {
    try {
      Field objectField = getFieldFromObject(object, field.getName()).orElseThrow();
      objectField.setAccessible(true);
      return Optional.ofNullable(objectField.get(object)).map(Object::toString).orElse(null);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Get string value from object with given field name
   *
   * @param fieldName target field name
   * @param object target object
   * @return String
   */
  public static String getStringFromObject(Object object, String fieldName) {
    try {
      Field objectField = getFieldFromObject(object, fieldName).orElseThrow();
      objectField.setAccessible(true);
      return Optional.ofNullable(objectField.get(object)).map(Object::toString).orElse(null);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Get field value from object at field
   *
   * @param object
   * @param fieldName
   * @return
   */
  public static Object getValueFromObject(Object object, String fieldName) {
    if (Objects.isNull(fieldName)) return null;
    if (fieldName.contains(".")) {
      String[] fieldNames = fieldName.split("\\.");
      if (fieldNames.length > 0) {
        Object result = getValueFromObjectByFieldName(object, fieldNames[0]);
        for (int i = 1; i < fieldNames.length; i++) {
          if (Objects.nonNull(result)) {
            result = getValueFromObjectByFieldName(result, fieldNames[i]);
          }
        }
        return result;
      }
    }
    return getValueFromObjectByFieldName(object, fieldName);
  }

  /**
   * Get value from object by field name
   *
   * @param object
   * @param fieldName
   * @return
   */
  public static Object getValueFromObjectByFieldName(Object object, String fieldName) {
    Field objectField = getFieldFromObject(object, fieldName).orElseThrow();
    return getValueFromObject(object, objectField);
  }

  /**
   * Get value from object by field
   *
   * @param object
   * @param field
   * @return
   */
  public static Object getValueFromObject(Object object, Field field) {
    try {
      field.setAccessible(true);
      return Optional.ofNullable(field.get(object))
          .map(value -> field.getType().cast(value))
          .orElse(null);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Set value to object by field
   *
   * @param object
   * @param field
   * @param value
   */
  public static void setValueToObject(Object object, Field field, Object value) {
    try {
      field.setAccessible(true);
      field.set(object, value);
    } catch (Exception ignored) {
    }
  }
}
