package com.teachermanagement.teacher_management.common.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.teachermanagement.teacher_management.common.exception.BadRequestException;
import com.teachermanagement.teacher_management.common.exception.UnexpectedException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableMap;
import com.teachermanagement.teacher_management.common.constant.IBaseErrorCode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class JsonUtils {
  private static final ObjectMapper mapper = ObjectMapperUtils.getInstance();

  /**
   * Convert string to json node
   *
   * @param rawJson
   * @return
   */
  public static JsonNode toJsonNode(String rawJson) {
    if (Objects.isNull(rawJson)) return null;
    try {
      return mapper.readTree(rawJson);
    } catch (JsonProcessingException e) {
      throw new BadRequestException(IBaseErrorCode.ERROR_JSON_PROCESSING);
    }
  }

  /**
   * Convert object to json node
   *
   * @param object
   * @return
   */
  public static JsonNode objectToJsonNode(Object object) {
    if (Objects.isNull(object)) return null;
    return mapper.valueToTree(object);
  }

  /**
   * Convert json node to string
   *
   * @param jsonNode
   * @return
   */
  public static String toJsonString(JsonNode jsonNode) {
    return Optional.ofNullable(jsonNode).map(JsonNode::toString).orElse(null);
  }

  /**
   * Convert string to object mapped by given type
   *
   * @param rawJson
   * @param type
   * @param <T>
   * @return
   */
  public static <T> T toObject(String rawJson, Class<T> type) {
    try {
      return mapper.readValue(rawJson, type);
    } catch (JsonProcessingException e) {
      throw new UnexpectedException(IBaseErrorCode.ERROR_JSON_PROCESSING);
    }
  }

  /**
   * Convert given object to json string
   *
   * @param object
   * @return
   */
  public static String toJsonString(Object object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new UnexpectedException(IBaseErrorCode.ERROR_JSON_PROCESSING);
    }
  }

  /**
   * Converts the given JsonNode to an object of the specified type.
   *
   * @param jsonNode The JsonNode containing the JSON data to be parsed.
   * @param type The class type to which the JSON data will be converted.
   * @param <T> The type of the object to return.
   * @return An object of the specified type, converted from the JsonNode data.
   * @throws UnexpectedException If an error occurs during JSON processing.
   */
  public static <T> T toObject(JsonNode jsonNode, Class<T> type) {
    try {
      return mapper.treeToValue(jsonNode, type);
    } catch (JsonProcessingException e) {
      throw new UnexpectedException(IBaseErrorCode.ERROR_JSON_PROCESSING);
    }
  }

  /**
   * Convert json node to map
   *
   * @param jsonNode
   * @return
   */
  @SuppressWarnings("unchecked")
  public static Map<String, String> toMap(JsonNode jsonNode) {
    return mapper.convertValue(jsonNode, Map.class);
  }

  /**
   * Convert map to json node
   *
   * @param map
   * @return
   */
  public static JsonNode toJsonNode(Map<String, String> map) {
    return mapper.valueToTree(map);
  }

  /**
   * Reads a JSON file from the resources folder and returns it as a JsonNode.
   *
   * @param resourcePath
   * @return
   * @throws IOException
   */
  public static JsonNode readJsonResource(String resourcePath) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    try (InputStream inputStream =
        JsonUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
      if (inputStream == null) {
        throw new UnexpectedException(
            IBaseErrorCode.ERROR_NOT_FOUND_RESOURCE_PATH,
            ImmutableMap.of("resourcePath", resourcePath));
      }
      return objectMapper.readTree(inputStream);
    }
  }

  /**
   * Compare tow json node whether they are equal or not
   *
   * @param node1
   * @param node2
   * @return Boolean
   */
  public static Boolean equals(JsonNode node1, JsonNode node2) {
    if (node1 == null || node2 == null) {
      return node1 == node2;
    }
    if (node1.isArray() && node2.isArray()) {
      List<String> list1 = toSortedList((ArrayNode) node1);
      List<String> list2 = toSortedList((ArrayNode) node2);
      return list1.equals(list2);
    }
    if (node1.isObject() && node2.isObject()) {
      return Objects.equals(node1, node2);
    }
    return Objects.equals(node1.toString(), node2.toString());
  }

  /**
   * Function used to sort List<String>
   *
   * @param array
   * @return List<String>
   */
  private static List<String> toSortedList(ArrayNode array) {
    List<String> list = new ArrayList<>();
    for (JsonNode node : array) {
      list.add(node.toString());
    }
    Collections.sort(list);
    return list;
  }
}
