BEGIN;

ALTER TABLE questionnaire.questionnaire_statuses ALTER COLUMN status TYPE VARCHAR;

COMMIT;
