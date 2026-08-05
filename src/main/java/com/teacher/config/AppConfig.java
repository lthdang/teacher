package com.teacher.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teacher.common.util.ModelMapperUtils;
import com.teacher.common.util.ObjectMapperUtils;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public ObjectMapper objectMapper() {
    return ObjectMapperUtils.getInstance();
  }

  @Bean
  public ModelMapper modelMapper() {
    return ModelMapperUtils.getInstance();
  }
}
