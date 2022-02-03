package com.elsevier.reactivetaskservice.domain;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.springframework.data.relational.core.mapping.Table;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

@Entity
@Table("task.task")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TaskEntity implements Persistable<UUID>, Serializable {

  @org.springframework.data.annotation.Id
  @Id
  @Column(nullable = false)
  private UUID id;

  @Column(nullable = false)
  private String description;

  @CreatedDate
  @Column(nullable = false)
  private Instant created;

  @LastModifiedDate
  private Instant modified;

  @Column(nullable = false)
  private JsonNode value;

  @Column(nullable = false)
  private String status;

  private String assignedTo;

  private Instant assignedOn;

  private String completedBy;

  @Override
  public boolean isNew() {
    return created == null;
  }
}


