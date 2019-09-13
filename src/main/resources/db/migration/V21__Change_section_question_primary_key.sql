BEGIN;

DROP TABLE questionnaire.sections_questions;

CREATE TABLE questionnaire.sections_questions (
   id UUID PRIMARY KEY,
   question_id UUID NOT NULL REFERENCES questionnaire.questions (id),
   section_id UUID NOT NULL REFERENCES questionnaire.sections (id) ON DELETE CASCADE,
   display_order INT NOT NULL,
   CONSTRAINT unique_question_per_section UNIQUE (question_id, section_id)
);

COMMIT;