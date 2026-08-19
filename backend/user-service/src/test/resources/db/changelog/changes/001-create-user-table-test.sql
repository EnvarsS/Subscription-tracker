--liquibase formatted sql
--changeset user-service:001-create-user-table

CREATE TABLE user_profiles (
    user_id             CHAR(36)      NOT NULL PRIMARY KEY,
    display_name        VARCHAR(100),
    preferred_currency  CHAR(3)       CHECK (preferred_currency REGEXP '^[A-Z]{3}$'),
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL
);