BEGIN;

-- This migration only works if the database has no data in it, otherwise we would need to manage the migration of
-- existing answers from referencing a template_question + section to just a section_question
-- at the time of running this migration we have a clean slate so we do not need to worry about existing data.

ALTER TABLE questionnaire.answers
    DROP COLUMN question_id;

ALTER TABLE questionnaire.answers
    DROP COLUMN section_id;

ALTER TABLE questionnaire.answers
    ADD COLUMN section_question_id UUID NOT NULL REFERENCES questionnaire.sections_questions (id) ON DELETE CASCADE;

COMMIT;
