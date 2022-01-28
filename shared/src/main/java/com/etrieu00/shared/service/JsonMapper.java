package com.etrieu00.shared.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.stereotype.Service;

@Service
public class JsonMapper {

  private final ObjectMapper mapper = new ObjectMapper()
    .registerModule(new JavaTimeModule())
    .registerModule(new Jdk8Module())
    .registerModule(new ParameterNamesModule())
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true)
    .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

  public <T> String convertToString(T entity) {
    try {
      return mapper.writeValueAsString(entity);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> T convertToObject(String payload, Class<T> type) {
    try {
      return mapper.readValue(payload, type);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}