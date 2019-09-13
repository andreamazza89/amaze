BEGIN;

CREATE TABLE questionnaire.requests_for_clarification
(
    id                  UUID PRIMARY KEY,
    section_question_id UUID      NOT NULL REFERENCES questionnaire.sections_questions (id) ON DELETE CASCADE,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    message             VARCHAR   NOT NULL
);

COMMIT;