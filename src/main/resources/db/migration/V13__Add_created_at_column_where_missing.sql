BEGIN;

ALTER TABLE questionnaire.questionnaires
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT now();

ALTER TABLE questionnaire.sections
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT now();

ALTER TABLE questionnaire.sections_questions
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT now();

ALTER TABLE questionnaire.questions
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT now();

COMMIT;