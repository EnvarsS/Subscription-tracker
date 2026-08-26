--liquibase formatted sql
--changeset notification-service:002-create-reminders-table

CREATE TABLE reminders (
    id         CHAR(36)      NOT NULL PRIMARY KEY,
    item_id    CHAR(36)      NOT NULL,
    user_id    CHAR(36)      NOT NULL,
    item_name  VARCHAR(120)  NOT NULL,
    due_date   DATE          NOT NULL,
    created_at TIMESTAMP     NOT NULL
);