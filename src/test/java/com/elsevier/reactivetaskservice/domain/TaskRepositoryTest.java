package com.elsevier.reactivetaskservice.domain;

import com.elsevier.reactivetaskservice.configuration.R2dbcConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration.LiquibaseConfiguration;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest()
@Testcontainers
@Import({R2dbcConfiguration.class, JacksonAutoConfiguration.class, LiquibaseAutoConfiguration.class})
class TaskRepositoryTest {

  @Container
  public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:12.5")
      .withDatabaseName("reactive-tasks")
      .withUsername("task_admin")
      .withPassword("password");

  @DynamicPropertySource
  static void postgresqlProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.r2dbc.url", TaskRepositoryTest::r2dbcUrl);
    registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
    registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);
    registry.add("spring.liquibase.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.liquibase.user", postgreSQLContainer::getUsername);
    registry.add("spring.liquibase.password", postgreSQLContainer::getPassword);
  }

  private static String r2dbcUrl() {
    return String.format("r2dbc:postgresql://%s:%s/%s", postgreSQLContainer.getContainerIpAddress(),
        postgreSQLContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
        postgreSQLContainer.getDatabaseName());
  }

  @Autowired
  private TaskRepository repo;

  @Autowired
  private ObjectMapper objectMapper = new ObjectMapper();

  private JsonNode taskValue;

  @BeforeEach
  public void setup() throws JsonProcessingException {
    taskValue = objectMapper.readTree("{ \"colour\": \"blue\" }");

    repo.save(TaskEntity.builder()
        .id(UUID.randomUUID())
        .description("Task 1")
        .status("Available")
        .value(taskValue)
        .build())
        .block();

    repo.save(TaskEntity.builder()
        .id(UUID.randomUUID())
        .description("Task 2")
        .status("Available")
        .value(taskValue)
        .build())
        .block();

    repo.save(TaskEntity.builder()
        .id(UUID.randomUUID())
        .description("Task 3")
        .status("Available")
        .value(taskValue)
        .build())
        .block();

    repo.save(TaskEntity.builder()
        .id(UUID.randomUUID())
        .description("Task 4")
        .status("Available")
        .value(taskValue)
        .build())
        .block();
  }

  @AfterEach
  public void tearDown() {
    repo.deleteAll().block();
  }

  @Test
  public void shouldRetrieveAll() {
    repo.count()
        .as(StepVerifier::create)
        .expectNext(4l)
        .expectComplete()
        .verify();
  }

  @Test
  public void shouldSortByStringField() {

    repo.findAll(Sort.by(Order.asc("description")))
        .as(StepVerifier::create)
        .expectNextMatches(t -> t.getDescription().equals("Task 1"))
        .expectNextMatches(t -> t.getDescription().equals("Task 2"))
        .expectNextMatches(t -> t.getDescription().equals("Task 3"))
        .expectNextMatches(t -> t.getDescription().equals("Task 4"))
        .expectComplete()
        .verify();

    repo.findAll(Sort.by(Order.desc("description")))
        .as(StepVerifier::create)
        .expectNextMatches(t -> t.getDescription().equals("Task 4"))
        .expectNextMatches(t -> t.getDescription().equals("Task 3"))
        .expectNextMatches(t -> t.getDescription().equals("Task 2"))
        .expectNextMatches(t -> t.getDescription().equals("Task 1"))
        .expectComplete()
        .verify();
  }

  @Test
  public void shouldSortByInstantField() {
    repo.findBy(PageRequest.of(1, 2, Sort.by(Order.desc("created"))))
        .as(StepVerifier::create)
        .expectNextMatches(t -> t.getDescription().equals("Task 2"))
        .expectNextMatches(t -> t.getDescription().equals("Task 1"))
        .expectComplete()
        .verify();
  }
}