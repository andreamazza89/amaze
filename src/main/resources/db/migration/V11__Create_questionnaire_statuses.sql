BEGIN;

CREATE TYPE due_diligence_questionnaire_status AS ENUM (
    'NEW',
    'NOT_STARTED',
    'IN_PROGRESS'
    );

CREATE TABLE questionnaire.questionnaire_statuses
(
    id               UUID PRIMARY KEY,
    questionnaire_id UUID NOT NULL REFERENCES questionnaire.questionnaires (id) ON DELETE CASCADE,
    status           due_diligence_questionnaire_status NOT NULL,
    created_at       TIMESTAMP                          NOT NULL
);

COMMIT;