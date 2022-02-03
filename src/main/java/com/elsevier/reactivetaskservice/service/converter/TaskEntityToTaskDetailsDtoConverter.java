package com.elsevier.reactivetaskservice.service.converter;

import com.elsevier.reactivetaskservice.api.dto.TaskDetailsDto;
import com.elsevier.reactivetaskservice.domain.TaskEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TaskEntityToTaskDetailsDtoConverter implements Converter<TaskEntity, TaskDetailsDto> {

  @Override
  public TaskDetailsDto convert(TaskEntity source) {
    return TaskDetailsDto.builder()
        .id(source.getId())
        .description(source.getDescription())
        .value(source.getValue().deepCopy())
        .status(source.getStatus())
        .assignedOn(source.getAssignedOn())
        .assignedTo(source.getAssignedTo())
        .completedBy(source.getCompletedBy())
        .created(source.getCreated())
        .modified(source.getModified())
        .build();
  }
}
