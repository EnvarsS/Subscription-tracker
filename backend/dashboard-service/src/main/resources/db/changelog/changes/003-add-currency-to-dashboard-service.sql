-- liquibase formatted sql
-- changeset dashboard-service:003-add-currency-to-dashboard-items

ALTER TABLE dashboard_items
    ADD COLUMN currency VARCHAR(3) NOT NULL DEFAULT 'EUR' CHECK (currency REGEXP '^[A-Z]{3}$');