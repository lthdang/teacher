package com.teacher.common.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public class ModelMapperUtils {
  private static final ModelMapper mapper;

  static {
    mapper = new ModelMapper();
    mapper
        .getConfiguration()
        .setAmbiguityIgnored(true)
        .setFullTypeMatchingRequired(true)
        .setMatchingStrategy(MatchingStrategies.STRICT);
  }

  public static ModelMapper getInstance() {
    return mapper;
  }
}
