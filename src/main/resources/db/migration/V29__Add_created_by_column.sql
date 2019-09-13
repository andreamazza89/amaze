BEGIN;

ALTER TABLE questionnaire.answers
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE questionnaire.answers
    ALTER COLUMN created_by_username DROP DEFAULT;

--

ALTER TABLE questionnaire.question_review_statuses
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE questionnaire.question_review_statuses
    ALTER COLUMN created_by_username DROP DEFAULT;

--

ALTER TABLE questionnaire.questionnaire_statuses
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE questionnaire.questionnaire_statuses
    ALTER COLUMN created_by_username DROP DEFAULT;

--

ALTER TABLE questionnaire.questionnaire_submission_dues
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE questionnaire.questionnaire_submission_dues
    ALTER COLUMN created_by_username DROP DEFAULT;

--

ALTER TABLE questionnaire.questionnaires
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE questionnaire.questionnaires
    ALTER COLUMN created_by_username DROP DEFAULT;

--

ALTER TABLE questionnaire.questions
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE questionnaire.questions
    ALTER COLUMN created_by_username DROP DEFAULT;

--

ALTER TABLE questionnaire.requests_for_clarification
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE questionnaire.requests_for_clarification
    ALTER COLUMN created_by_username DROP DEFAULT;

--

ALTER TABLE questionnaire.section_assignees
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE questionnaire.section_assignees
    ALTER COLUMN created_by_username DROP DEFAULT;

--

ALTER TABLE questionnaire.section_statuses
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE questionnaire.section_statuses
    ALTER COLUMN created_by_username DROP DEFAULT;

--

ALTER TABLE questionnaire.sections
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE questionnaire.sections
    ALTER COLUMN created_by_username DROP DEFAULT;

--

ALTER TABLE questionnaire.sections_questions
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE questionnaire.sections_questions
    ALTER COLUMN created_by_username DROP DEFAULT;

--

ALTER TABLE questionnaire.template_sections
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE questionnaire.template_sections
    ALTER COLUMN created_by_username DROP DEFAULT;

--

ALTER TABLE questionnaire.template_sections_questions
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE questionnaire.template_sections_questions
    ALTER COLUMN created_by_username DROP DEFAULT;

--

ALTER TABLE questionnaire.templates
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE questionnaire.templates
    ALTER COLUMN created_by_username DROP DEFAULT;

--

ALTER TABLE supporting_documents.documents
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE supporting_documents.documents
    ALTER COLUMN created_by_username DROP DEFAULT;

--

ALTER TABLE supporting_documents.files
    ADD COLUMN created_by_username VARCHAR NOT NULL DEFAULT 'system';

ALTER TABLE supporting_documents.files
    ALTER COLUMN created_by_username DROP DEFAULT;

--

COMMIT;
