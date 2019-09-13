BEGIN;

CREATE SCHEMA supporting_documents;

CREATE TABLE supporting_documents.files
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    data       bytea     NOT NULL
);

CREATE TABLE supporting_documents.documents
(
    id                   UUID PRIMARY KEY,
    created_at           TIMESTAMP NOT NULL DEFAULT now(),
    file_id              UUID      NOT NULL REFERENCES supporting_documents.files (id) ON DELETE CASCADE,
    section_question_id  UUID      NOT NULL REFERENCES questionnaire.sections_questions (id) ON DELETE CASCADE,
    provided_as_required BOOLEAN   NOT NULL,
    file_name            VARCHAR   NOT NULL,
    file_type            VARCHAR   NOT NULL,
    CONSTRAINT unique_file UNIQUE (file_id)
);

COMMIT;
