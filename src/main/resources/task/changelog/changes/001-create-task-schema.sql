--liquibase formatted sql
--changeset DY:create-task-schema
CREATE SCHEMA task
--rollback DROP SCHEMA task;
