BEGIN;

CREATE TABLE questionnaire.section_assignees
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP NOT NULL,
    username    VARCHAR   NOT NULL,
    section_id  UUID      NOT NULL REFERENCES questionnaire.sections (id) ON DELETE CASCADE
);

COMMIT;
