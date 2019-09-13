BEGIN;

CREATE TABLE questionnaire.question_review_statuses
(
    id                  UUID PRIMARY KEY,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    section_question_id UUID      NOT NULL REFERENCES questionnaire.sections_questions (id) ON DELETE CASCADE,
    status              VARCHAR   NOT NULL
);

COMMIT;