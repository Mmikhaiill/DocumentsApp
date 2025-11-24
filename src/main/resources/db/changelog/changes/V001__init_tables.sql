-- src/main/resources/db/changelog/changes/V001__init_tables.sql
--liquibase formatted sql

--changeset init:1 author:you
--comment: Создание таблиц документов, спецификаций и лога дубликатов

CREATE TABLE document (
                          id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          number VARCHAR(50) NOT NULL UNIQUE,
                          date DATE NOT NULL,
                          amount NUMERIC(19,2) NOT NULL DEFAULT 0,
                          note TEXT
);

CREATE TABLE specification (
                               id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               document_id BIGINT NOT NULL REFERENCES document(id) ON DELETE CASCADE,
                               name VARCHAR(255) NOT NULL,
                               amount NUMERIC(19,2) NOT NULL
);

CREATE TABLE duplicate_log (
                               id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               entity_type VARCHAR(20) NOT NULL,
                               duplicate_value VARCHAR(255) NOT NULL,
                               timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               context TEXT
);

--rollback DROP TABLE duplicate_log;
--rollback DROP TABLE specification;
--rollback DROP TABLE document;