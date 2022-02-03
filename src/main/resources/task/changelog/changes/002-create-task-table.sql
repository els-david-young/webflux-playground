--liquibase formatted sql
--changeset DY:create-task-table
CREATE TABLE task.task
(
    id              UUID                        NOT NULL,
    description     TEXT                        NOT NULL,
    created         timestamp                   NOT NULL,
    modified        timestamp,
    value           JSONB                       NOT NULL,
    status          TEXT DEFAULT 'available'    NOT NULL,
    assigned_to     TEXT,
    assigned_on     timestamp,
    completed_by    TEXT,
    CONSTRAINT TASK_PK PRIMARY KEY (id)
);
--rollback DROP TABLE task;
