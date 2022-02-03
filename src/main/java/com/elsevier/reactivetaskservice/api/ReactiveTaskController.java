package com.elsevier.reactivetaskservice.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

import com.elsevier.reactivetaskservice.api.dto.CreateTaskDto;
import com.elsevier.reactivetaskservice.api.dto.TaskDetailsDto;
import com.elsevier.reactivetaskservice.api.dto.TaskUpdateDto;
import com.elsevier.reactivetaskservice.domain.TaskEntity;
import com.elsevier.reactivetaskservice.domain.TaskRepository;
import com.elsevier.reactivetaskservice.service.TaskService;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequestMapping("/task")
@RestController
public class ReactiveTaskController {

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private TaskService taskService;

  @GetMapping("")
  public Flux<TaskEntity> getAllTasks() {
    return taskRepository.findAll();
  }

  @PostMapping(consumes = "application/json", produces = APPLICATION_JSON_VALUE)
  public Mono<UUID> createTask(@Valid @RequestBody CreateTaskDto task) {
    log.info("Received request to create task description {}", task.getDescription());
    return taskService.createTask(task)
        .map(TaskDetailsDto::getId);
  }

  @PostMapping(path = "/{id}", consumes = "application/json", produces = APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<Object>> updateTask(@Valid @RequestBody Mono<TaskUpdateDto> task, @PathVariable("id") UUID id) {
    log.info("Received request to update task id {}", id);
    return taskService.updateTask(id, task)
        .map(t -> ResponseEntity.noContent().build())
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<TaskDetailsDto>> getTask(@PathVariable("id") UUID id) {
    log.info("Received request to retrieve task - {}", id);
    return taskService.getTaskDetails(id)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @PostMapping(path = "/updates", consumes = APPLICATION_JSON_VALUE,
      produces = TEXT_EVENT_STREAM_VALUE)
  public Flux<TaskDetailsDto> getTaskUpdates(@RequestBody List<UUID> ids) {
    log.info("Received subscription to task updates for {}", ids);
    return taskService.getTaskUpdates(ids);
  }

}
