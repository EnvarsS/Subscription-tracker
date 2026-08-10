--liquibase formatted sql
--changeset item-service:001-create-recurring-items-table

CREATE TABLE recurring_items (
    id              CHAR(36)      NOT NULL PRIMARY KEY,
    user_id         CHAR(36)      NOT NULL,
    name            VARCHAR(120)  NOT NULL,
    type            VARCHAR(20)   NOT NULL CHECK (type IN ('SUBSCRIPTION', 'BILL', 'SAVING')),
    amount          DECIMAL(12,2) NOT NULL,
    currency        VARCHAR(3)    NOT NULL CHECK (currency REGEXP '^[A-Z]{3}$'),
    billing_cycle   VARCHAR(20)   NOT NULL CHECK (billing_cycle IN ('WEEKLY', 'MONTHLY', 'YEARLY')),
    next_due_date   DATE          NOT NULL,
    active          BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP     NOT NULL,
    updated_at      TIMESTAMP     NOT NULL
);