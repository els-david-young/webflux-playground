package com.elsevier.reactivetaskservice.configuration;

import com.elsevier.reactivetaskservice.domain.converter.PostgresJsonToJsonNodeConverter;
import com.elsevier.reactivetaskservice.domain.converter.JsonNodeToPostgresJsonConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.PostgresDialect;

@Configuration
public class R2dbcConfiguration {

  @Bean
  public R2dbcCustomConversions customConversions(ObjectMapper mapper) {
    List<Converter<?, ?>> converters = new ArrayList<>();
    converters.add(new PostgresJsonToJsonNodeConverter(mapper));
    converters.add(new JsonNodeToPostgresJsonConverter(mapper));
    return R2dbcCustomConversions.of(PostgresDialect.INSTANCE, converters);
  }
}
