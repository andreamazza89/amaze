BEGIN;

CREATE TABLE questionnaire.questionnaire_submission_dues
(
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP NOT NULL DEFAULT now(),
    questionnaire_id UUID      NOT NULL REFERENCES questionnaire.questionnaires (id) ON DELETE CASCADE,
    weeks            INT       NOT NULL,
    CONSTRAINT unique_questionnaire UNIQUE (questionnaire_id)
);

COMMIT;