package com.elsevier.reactivetaskservice.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class TaskDetailsDto {

  UUID id;

  String description;

  Instant created;

  Instant modified;

  JsonNode value;

  String status;

  String assignedTo;

  Instant assignedOn;

  String completedBy;
}
