package com.elsevier.reactivetaskservice.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class CreateTaskDto {

  @Size(max = 256, min = 3)
  @NotBlank
  String description;

  @NotNull
  JsonNode value;

}
