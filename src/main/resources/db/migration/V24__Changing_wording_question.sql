BEGIN;

UPDATE questionnaire.questions
SET label = 'Please provide the name of the individual responsible for internal audit, or if outsourced, the name of the external company'
WHERE id = '123e4567-e89b-12d3-a456-426655440413';

COMMIT;