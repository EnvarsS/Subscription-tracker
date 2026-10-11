--liquibase formatted sql
--changeset dashboard-service:001-create-dashboard-table.sql

CREATE TABLE dashboard_items (
    item_id       CHAR(36)      NOT NULL PRIMARY KEY,
    user_id       CHAR(36)      NOT NULL,
    item_type     VARCHAR(20)   NOT NULL,
    amount        DECIMAL(12,2) NOT NULL,
    billing_cycle VARCHAR(20)   NOT NULL,
    active        BOOLEAN       NOT NULL DEFAULT TRUE,
    updated_at    TIMESTAMP     NOT NULL
);