BEGIN;

CREATE TABLE questionnaire.templates
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP DEFAULT now(),
    name       VARCHAR NOT NULL UNIQUE
);

CREATE TABLE questionnaire.template_sections
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP DEFAULT now(),
    title       VARCHAR NOT NULL,
    template_id UUID    NOT NULL REFERENCES questionnaire.templates (id) ON DELETE CASCADE,
    UNIQUE (template_id, title)
);

CREATE TABLE questionnaire.template_sections_questions
(
    created_at          TIMESTAMP DEFAULT now(),
    question_id         UUID NOT NULL REFERENCES questionnaire.questions (id),
    template_section_id UUID NOT NULL REFERENCES questionnaire.template_sections (id) ON DELETE CASCADE,
    PRIMARY KEY (question_id, template_section_id)
);


COMMIT;
