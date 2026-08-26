--liquibase formatted sql
--changeset notification-service:001-create-tracked-items-table

CREATE TABLE tracked_items (
    item_id       CHAR(36)      NOT NULL PRIMARY KEY,
    user_id       CHAR(36)      NOT NULL,
    name          VARCHAR(120)  NOT NULL,
    next_due_date DATE          NOT NULL,
    active        BOOLEAN       NOT NULL DEFAULT TRUE,
    updated_at    TIMESTAMP     NOT NULL
);