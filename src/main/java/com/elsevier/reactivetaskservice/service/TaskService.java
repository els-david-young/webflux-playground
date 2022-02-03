package com.elsevier.reactivetaskservice.service;

import com.elsevier.reactivetaskservice.api.dto.CreateTaskDto;
import com.elsevier.reactivetaskservice.api.dto.TaskDetailsDto;
import com.elsevier.reactivetaskservice.api.dto.TaskUpdateDto;
import com.elsevier.reactivetaskservice.domain.TaskEntity;
import com.elsevier.reactivetaskservice.domain.TaskRepository;
import com.elsevier.reactivetaskservice.service.converter.CreateTaskDtoToTaskEntityConverter;
import com.elsevier.reactivetaskservice.service.converter.TaskEntityToTaskDetailsDtoConverter;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TaskService {

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private TaskRepository repository;

  @Autowired
  private CreateTaskDtoToTaskEntityConverter createTaskDtoToTaskEntityConverter;

  @Autowired
  private TaskEntityToTaskDetailsDtoConverter taskEntityToTaskDetailsDtoConverter;

  public Mono<TaskDetailsDto> createTask(@NotNull CreateTaskDto taskDto) {
    TaskEntity taskEntity = createTaskDtoToTaskEntityConverter.convert(taskDto);
    if (taskEntity == null) {
      throw new IllegalArgumentException("Failed to convert CreateTaskDto to TaskEntity");
    }
    taskEntity.setId(UUID.randomUUID());
    taskEntity.setStatus("available");

    return repository.save(taskEntity)
        .map(this::toTaskDetailsDto);
  }

  public Mono<TaskDetailsDto> updateTask(@NotNull UUID id, @NotNull Mono<TaskUpdateDto> updateMono) {
    return repository.findById(id)
        .flatMap(task -> updateMono.map(u -> {
          if (u.getDescription() != null) {
            task.setDescription(u.getDescription());
          }
          if (u.getValue() != null) {
            task.setValue(u.getValue());
          }
          return task;
        }))
        .flatMap(task -> repository.save(task))
        .doOnNext(entity -> {
          cacheManager.getCache("task-updates").put(entity.getId(), entity);
          log.info("Cached updated task entity ID: {}", entity.getId());
        })
        .map(this::toTaskDetailsDto);
  }

  public Mono<TaskDetailsDto> getTaskDetails(@NotNull UUID id) throws EntityNotFoundException {
    return repository.findById(id)
        .map(this::toTaskDetailsDto);
  }

  public Flux<TaskDetailsDto> getTaskUpdates(Collection<UUID> ids) {
    AtomicReference<Instant> lastPollTime = new AtomicReference<>(Instant.now());
    final Cache cache = cacheManager.getCache("task-updates");
    assert cache != null;

    return Flux.interval(Duration.ofSeconds(5)).flatMapIterable(seq -> {
      Instant now = Instant.now();
      List<TaskDetailsDto> updatedTasks = ids.stream()
          .map(cache::get)
          .filter(Objects::nonNull)
          .map(cacheValue -> (TaskEntity) cacheValue.get())
          .filter(task -> task.getModified().compareTo(lastPollTime.get()) >= 0)
          .map(this::toTaskDetailsDto)
          .collect(Collectors.toList());
      lastPollTime.set(now);
      return updatedTasks;
    });
  }

  private TaskDetailsDto toTaskDetailsDto(TaskEntity entity) {
    TaskDetailsDto converted = taskEntityToTaskDetailsDtoConverter.convert(entity);
    if (converted == null) {
      throw new IllegalArgumentException("Failed to convert TaskEntity to TaskDetailsDto");
    }
    return converted;
  }
}
