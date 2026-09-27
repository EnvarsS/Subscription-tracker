--LIQUIBASE FORMATTED SQL
--changeset dashboard-service:002-create-user-preference-table

CREATE TABLE user_preference (
    user_id CHAR(36) PRIMARY KEY,
    currency VARCHAR(3) CHECK,
    updated_at TIMESTAMP NOT NULL
)