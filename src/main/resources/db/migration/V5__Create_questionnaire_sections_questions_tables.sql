BEGIN;

CREATE TABLE questionnaire.questionnaires (
   id UUID PRIMARY KEY,
   name VARCHAR NOT NULL,
   partner_id VARCHAR NOT NULL,
   UNIQUE (partner_id, name)
);

CREATE TABLE questionnaire.sections (
   id UUID PRIMARY KEY,
   questionnaire_id UUID NOT NULL REFERENCES questionnaire.questionnaires (id) ON DELETE CASCADE,
   title VARCHAR NOT NULL,
   UNIQUE (questionnaire_id, title)
);

CREATE TABLE questionnaire.questions (
   id UUID PRIMARY KEY,
   label TEXT NOT NULL
);

CREATE TABLE questionnaire.sections_questions (
   question_id UUID NOT NULL REFERENCES questionnaire.questions (id),
   section_id UUID NOT NULL REFERENCES questionnaire.sections (id) ON DELETE CASCADE,
   PRIMARY KEY (question_id, section_id)
);

COMMIT;
