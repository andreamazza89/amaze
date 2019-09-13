BEGIN;

UPDATE questionnaire.questions
SET label = 'Do you plan on having marketing material that includes the name of insurance carriers you will be working with?'
WHERE id = '113e4567-e89b-12d3-a456-426655440306';

UPDATE questionnaire.questions
SET label = 'Please provide the name of the accounting software you are using and how does it (or will it) interface with a policy issuance system.'
WHERE id = '113e4567-e89b-12d3-a456-426655440416';

UPDATE questionnaire.questions
SET label = 'Please provide the name & address of your financial auditor'
WHERE id = '123e4567-e89b-12d3-a456-426655440205';

UPDATE questionnaire.questions
SET label = 'If yes, please provide details of the firm used including name and address'
WHERE id = '123e4567-e89b-12d3-a456-426655440214';

UPDATE questionnaire.questions
SET label = 'If yes, please provide details of the firm used including name and address'
WHERE id = '123e4567-e89b-12d3-a456-426655440220';

UPDATE questionnaire.questions
SET label = 'Please name your Data Protection Officer or the person responsible for reporting to the Data Protection Commissioner'
WHERE id = '123e4567-e89b-12d3-a456-426655440411';

UPDATE questionnaire.questions
SET label = 'Please name the person most responsible for overseeing complaints resolution and managing the complaints log'
WHERE id = '123e4567-e89b-12d3-a456-426655440412';

UPDATE questionnaire.questions
SET label = 'Please provide the name of the individual responsible for internal audit, or if outsourced, the title of the external company'
WHERE id = '123e4567-e89b-12d3-a456-426655440413';

UPDATE questionnaire.questions
SET label = 'Upload a copy of your General Data Protection Regulation (GDPR) policies / procedures'
WHERE id = '123e4567-e89b-12d3-a456-426655440426';

UPDATE questionnaire.questions
SET label = 'Upload a schedule of your policies to be underwritten by GLUK to include: product name; scheme name; class of business; commercial or retail indicator; distribution channel; date of last review & % of overall business'
WHERE id = '123e4567-e89b-12d3-a456-426655440441';

UPDATE questionnaire.questions
SET label = 'Upload your Policy literature (including Key Facts) for all your products & schemes'
WHERE id = '123e4567-e89b-12d3-a456-426655440442';

UPDATE questionnaire.questions
SET label = 'Do you have any delegated authority or AR/IAR relationships? If yes, please refer to appendix 1, supply the appropriate additional documentation and complete the following questions'
WHERE id = '123e4567-e89b-12d3-a456-426655440446';

UPDATE questionnaire.questions
SET label = 'Describe all the authorities delegated to your sub agent(s), AR(s) and IAR(s) including full limits (i.e. binding authority), claims authority and any other reporting responsibilities'
WHERE id = '123e4567-e89b-12d3-a456-426655440448';

UPDATE questionnaire.questions
SET label = 'Upload Board Meeting Schedules (Frequencies), Agenda and Minutes'
WHERE id = '123e4567-e89b-12d3-a456-426655440471';

UPDATE questionnaire.questions
SET label = 'Is your front end and PAS infrastructure cloud based or on premise? If on premise, what security measures do you have in place both against environmental issues (flooding, power cuts, etc.) as well as in terms of security'
WHERE id = '123e4567-e89b-12d3-a456-426655440503';

UPDATE questionnaire.questions
SET label = 'Do you have a process to regularly review your applications/systems security (e.g. penetration testing)? If yes, please provide details'
WHERE id = '123e4567-e89b-12d3-a456-426655440532';

UPDATE questionnaire.questions
SET label = 'How often do you intend to audit or review your sub agent(s), AR(s) and IAR(s)?'
WHERE id = '123e4567-e89b-12d3-a456-426655440603';

COMMIT;
