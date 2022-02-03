package com.elsevier.reactivetaskservice.service.converter;

import com.elsevier.reactivetaskservice.api.dto.CreateTaskDto;
import com.elsevier.reactivetaskservice.domain.TaskEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CreateTaskDtoToTaskEntityConverter implements Converter<CreateTaskDto, TaskEntity> {

  @Override
  public TaskEntity convert(CreateTaskDto source) {
    return TaskEntity.builder()
        .description(source.getDescription())
        .value(source.getValue())
        .build();
  }
}
