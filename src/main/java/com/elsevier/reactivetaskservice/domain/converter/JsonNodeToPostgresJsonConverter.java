package com.elsevier.reactivetaskservice.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class JsonNodeToPostgresJsonConverter implements Converter<JsonNode, Json> {

  private ObjectMapper objectMapper;

  public JsonNodeToPostgresJsonConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Json convert(JsonNode source) {
    try {
      String jsonStr = objectMapper.writeValueAsString(source);
      return Json.of(jsonStr);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("failed to convert Json formats for persistence", e);
    }
  }
}
