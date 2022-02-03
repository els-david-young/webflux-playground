package com.elsevier.reactivetaskservice.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class PostgresJsonToJsonNodeConverter implements Converter<Json, JsonNode> {

  ObjectMapper objectMapper;

  public PostgresJsonToJsonNodeConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public JsonNode convert(Json source) {
    try {
      return objectMapper.readTree(source.asString());
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("failed to convert Json value from store", e);
    }
  }
}
