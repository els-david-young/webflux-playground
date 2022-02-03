package com.elsevier.reactivetaskservice.domain;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TaskRepository extends ReactiveSortingRepository<TaskEntity, UUID> {

  Flux<TaskEntity> findBy(Pageable pageable);
}
