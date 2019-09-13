BEGIN;

CREATE TABLE questionnaire.answers
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP NOT NULL,
    given_answer VARCHAR   NOT NULL,
    section_id   UUID      NOT NULL REFERENCES questionnaire.sections (id) ON DELETE CASCADE,
    question_id  UUID      NOT NULL REFERENCES questionnaire.questions (id)
);

COMMIT;