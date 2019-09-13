BEGIN;

-- Demo Questions

INSERT INTO questionnaire.questions (id, label, requires_supporting_documents)
VALUES ('123e4567-e89b-12d3-a456-426655440901', 'Do you have previous experience with baking insurance?', false),
       ('123e4567-e89b-12d3-a456-426655440902', 'Have you ever insured a bakery?', false),
       ('123e4567-e89b-12d3-a456-426655440903', 'Please provide evidence of charitable donations.', true),

       ('123e4567-e89b-12d3-a456-426655440904', 'How much do you spend in team meals?', false),
       ('123e4567-e89b-12d3-a456-426655440905', 'Please provide financial statements for the last two years.', true),

       ('123e4567-e89b-12d3-a456-426655440906', 'Describe your product.', false),
       ('123e4567-e89b-12d3-a456-426655440907', 'Have you considered the competition?', false),
       ('123e4567-e89b-12d3-a456-426655440908', 'Please upload your proposed policy wording for this product.', true);

-- Demo Template

INSERT INTO questionnaire.templates (id, name)
VALUES ('e552d8e6-122f-4232-934b-860d5ff98e95', 'Demo Template');

-- Demo Sections

INSERT INTO questionnaire.template_sections (id, title, template_id, display_order)
VALUES ('e552d8e6-112f-4232-934b-860d3ff15e99', 'Previous experience', 'e552d8e6-122f-4232-934b-860d5ff98e95', 0);

INSERT INTO questionnaire.template_sections_questions (question_id, template_section_id, display_order)
VALUES ('123e4567-e89b-12d3-a456-426655440901', 'e552d8e6-112f-4232-934b-860d3ff15e99', 0),
       ('123e4567-e89b-12d3-a456-426655440902', 'e552d8e6-112f-4232-934b-860d3ff15e99', 1),
       ('123e4567-e89b-12d3-a456-426655440903', 'e552d8e6-112f-4232-934b-860d3ff15e99', 2);


INSERT INTO questionnaire.template_sections (id, title, template_id, display_order)
VALUES ('e552d8e7-112f-4232-934b-860d3ff15e99', 'Financial', 'e552d8e6-122f-4232-934b-860d5ff98e95', 1);

INSERT INTO questionnaire.template_sections_questions (question_id, template_section_id, display_order)
VALUES ('123e4567-e89b-12d3-a456-426655440904', 'e552d8e7-112f-4232-934b-860d3ff15e99', 0),
       ('123e4567-e89b-12d3-a456-426655440905', 'e552d8e7-112f-4232-934b-860d3ff15e99', 1);


INSERT INTO questionnaire.template_sections (id, title, template_id, display_order)
VALUES ('e552d8e8-112f-4232-934b-860d3ff15e99', 'Product Information', 'e552d8e6-122f-4232-934b-860d5ff98e95', 2);

INSERT INTO questionnaire.template_sections_questions (question_id, template_section_id, display_order)
VALUES ('123e4567-e89b-12d3-a456-426655440906', 'e552d8e8-112f-4232-934b-860d3ff15e99', 0),
       ('123e4567-e89b-12d3-a456-426655440907', 'e552d8e8-112f-4232-934b-860d3ff15e99', 1),
       ('123e4567-e89b-12d3-a456-426655440908', 'e552d8e8-112f-4232-934b-860d3ff15e99', 2);

COMMIT;
