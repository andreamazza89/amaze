BEGIN;

CREATE TABLE questionnaire.section_statuses
(
    id         UUID PRIMARY KEY,
    section_id UUID      NOT NULL REFERENCES questionnaire.sections (id) ON DELETE CASCADE,
    status     VARCHAR   NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

COMMIT;
