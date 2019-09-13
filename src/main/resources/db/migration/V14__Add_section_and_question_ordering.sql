BEGIN;

ALTER TABLE questionnaire.template_sections
    ADD COLUMN display_order INTEGER NOT NULL DEFAULT 0;

ALTER TABLE questionnaire.template_sections_questions
    ADD COLUMN display_order INTEGER NOT NULL DEFAULT 0;

ALTER TABLE questionnaire.sections
    ADD COLUMN display_order INTEGER NOT NULL DEFAULT 0;

ALTER TABLE questionnaire.sections_questions
    ADD COLUMN display_order INTEGER NOT NULL DEFAULT 0;

UPDATE questionnaire.template_sections
    SET display_order = 1
    WHERE id = 'e552d9e6-112f-4232-934b-860d4ff87f94';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '123e4567-e89b-12d3-a456-426655440101';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '123e4567-e89b-12d3-a456-426655440102';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '123e4567-e89b-12d3-a456-426655440103';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '123e4567-e89b-12d3-a456-426655440104';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '123e4567-e89b-12d3-a456-426655440105';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '123e4567-e89b-12d3-a456-426655440106';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '123e4567-e89b-12d3-a456-426655440107';

-- UK AR Questionnaire Template (Finance section)

UPDATE questionnaire.template_sections
    SET display_order = 2
    WHERE id = 'e552d9e6-112f-4232-934b-860d4ff87f95';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '123e4567-e89b-12d3-a456-426655440201';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '123e4567-e89b-12d3-a456-426655440202';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '123e4567-e89b-12d3-a456-426655440203';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '123e4567-e89b-12d3-a456-426655440204';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '123e4567-e89b-12d3-a456-426655440205';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '123e4567-e89b-12d3-a456-426655440206';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '123e4567-e89b-12d3-a456-426655440207';
UPDATE questionnaire.template_sections_questions SET display_order = 8 WHERE question_id = '123e4567-e89b-12d3-a456-426655440208';
UPDATE questionnaire.template_sections_questions SET display_order = 9 WHERE question_id = '123e4567-e89b-12d3-a456-426655440209';
UPDATE questionnaire.template_sections_questions SET display_order = 10 WHERE question_id = '123e4567-e89b-12d3-a456-426655440210';
UPDATE questionnaire.template_sections_questions SET display_order = 11 WHERE question_id = '123e4567-e89b-12d3-a456-426655440211';
UPDATE questionnaire.template_sections_questions SET display_order = 12 WHERE question_id = '123e4567-e89b-12d3-a456-426655440212';
UPDATE questionnaire.template_sections_questions SET display_order = 13 WHERE question_id = '123e4567-e89b-12d3-a456-426655440213';
UPDATE questionnaire.template_sections_questions SET display_order = 14 WHERE question_id = '123e4567-e89b-12d3-a456-426655440214';
UPDATE questionnaire.template_sections_questions SET display_order = 15 WHERE question_id = '123e4567-e89b-12d3-a456-426655440215';
UPDATE questionnaire.template_sections_questions SET display_order = 16 WHERE question_id = '123e4567-e89b-12d3-a456-426655440216';
UPDATE questionnaire.template_sections_questions SET display_order = 17 WHERE question_id = '123e4567-e89b-12d3-a456-426655440217';
UPDATE questionnaire.template_sections_questions SET display_order = 18 WHERE question_id = '123e4567-e89b-12d3-a456-426655440218';
UPDATE questionnaire.template_sections_questions SET display_order = 19 WHERE question_id = '123e4567-e89b-12d3-a456-426655440219';
UPDATE questionnaire.template_sections_questions SET display_order = 20 WHERE question_id = '123e4567-e89b-12d3-a456-426655440220';
UPDATE questionnaire.template_sections_questions SET display_order = 21 WHERE question_id = '123e4567-e89b-12d3-a456-426655440221';
UPDATE questionnaire.template_sections_questions SET display_order = 22 WHERE question_id = '123e4567-e89b-12d3-a456-426655440222';
UPDATE questionnaire.template_sections_questions SET display_order = 23 WHERE question_id = '123e4567-e89b-12d3-a456-426655440223';
UPDATE questionnaire.template_sections_questions SET display_order = 24 WHERE question_id = '123e4567-e89b-12d3-a456-426655440224';
UPDATE questionnaire.template_sections_questions SET display_order = 25 WHERE question_id = '123e4567-e89b-12d3-a456-426655440225';
UPDATE questionnaire.template_sections_questions SET display_order = 26 WHERE question_id = '123e4567-e89b-12d3-a456-426655440226';
UPDATE questionnaire.template_sections_questions SET display_order = 27 WHERE question_id = '123e4567-e89b-12d3-a456-426655440227';

-- UK AR Questionnaire Template (Commercial section)

UPDATE questionnaire.template_sections
    SET display_order = 3
    WHERE id = 'e552d9e6-112f-4232-934b-860d4ff87f96';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '123e4567-e89b-12d3-a456-426655440301';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '123e4567-e89b-12d3-a456-426655440302';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '123e4567-e89b-12d3-a456-426655440303';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '123e4567-e89b-12d3-a456-426655440304';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '123e4567-e89b-12d3-a456-426655440305';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '123e4567-e89b-12d3-a456-426655440306';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '123e4567-e89b-12d3-a456-426655440307';
UPDATE questionnaire.template_sections_questions SET display_order = 8 WHERE question_id = '123e4567-e89b-12d3-a456-426655440308';
UPDATE questionnaire.template_sections_questions SET display_order = 9 WHERE question_id = '123e4567-e89b-12d3-a456-426655440309';
UPDATE questionnaire.template_sections_questions SET display_order = 10 WHERE question_id = '123e4567-e89b-12d3-a456-426655440310';
UPDATE questionnaire.template_sections_questions SET display_order = 11 WHERE question_id = '123e4567-e89b-12d3-a456-426655440311';

-- UK AR Questionnaire Template (Compliance section)

UPDATE questionnaire.template_sections
    SET display_order = 4
    WHERE id = 'e552d9e6-112f-4232-934b-860d4ff87f97';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '123e4567-e89b-12d3-a456-426655440401';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '123e4567-e89b-12d3-a456-426655440402';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '123e4567-e89b-12d3-a456-426655440403';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '123e4567-e89b-12d3-a456-426655440404';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '123e4567-e89b-12d3-a456-426655440405';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '123e4567-e89b-12d3-a456-426655440406';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '123e4567-e89b-12d3-a456-426655440407';
UPDATE questionnaire.template_sections_questions SET display_order = 8 WHERE question_id = '123e4567-e89b-12d3-a456-426655440408';
UPDATE questionnaire.template_sections_questions SET display_order = 9 WHERE question_id = '123e4567-e89b-12d3-a456-426655440409';
UPDATE questionnaire.template_sections_questions SET display_order = 10 WHERE question_id = '123e4567-e89b-12d3-a456-426655440410';
UPDATE questionnaire.template_sections_questions SET display_order = 11 WHERE question_id = '123e4567-e89b-12d3-a456-426655440411';
UPDATE questionnaire.template_sections_questions SET display_order = 12 WHERE question_id = '123e4567-e89b-12d3-a456-426655440412';
UPDATE questionnaire.template_sections_questions SET display_order = 13 WHERE question_id = '123e4567-e89b-12d3-a456-426655440413';
UPDATE questionnaire.template_sections_questions SET display_order = 14 WHERE question_id = '123e4567-e89b-12d3-a456-426655440414';
UPDATE questionnaire.template_sections_questions SET display_order = 15 WHERE question_id = '123e4567-e89b-12d3-a456-426655440415';
UPDATE questionnaire.template_sections_questions SET display_order = 16 WHERE question_id = '123e4567-e89b-12d3-a456-426655440416';
UPDATE questionnaire.template_sections_questions SET display_order = 17 WHERE question_id = '123e4567-e89b-12d3-a456-426655440417';
UPDATE questionnaire.template_sections_questions SET display_order = 18 WHERE question_id = '123e4567-e89b-12d3-a456-426655440418';
UPDATE questionnaire.template_sections_questions SET display_order = 19 WHERE question_id = '123e4567-e89b-12d3-a456-426655440419';
UPDATE questionnaire.template_sections_questions SET display_order = 20 WHERE question_id = '123e4567-e89b-12d3-a456-426655440420';
UPDATE questionnaire.template_sections_questions SET display_order = 21 WHERE question_id = '123e4567-e89b-12d3-a456-426655440421';
UPDATE questionnaire.template_sections_questions SET display_order = 22 WHERE question_id = '123e4567-e89b-12d3-a456-426655440422';
UPDATE questionnaire.template_sections_questions SET display_order = 23 WHERE question_id = '123e4567-e89b-12d3-a456-426655440423';
UPDATE questionnaire.template_sections_questions SET display_order = 24 WHERE question_id = '123e4567-e89b-12d3-a456-426655440424';
UPDATE questionnaire.template_sections_questions SET display_order = 25 WHERE question_id = '123e4567-e89b-12d3-a456-426655440425';
UPDATE questionnaire.template_sections_questions SET display_order = 26 WHERE question_id = '123e4567-e89b-12d3-a456-426655440426';
UPDATE questionnaire.template_sections_questions SET display_order = 27 WHERE question_id = '123e4567-e89b-12d3-a456-426655440427';
UPDATE questionnaire.template_sections_questions SET display_order = 28 WHERE question_id = '123e4567-e89b-12d3-a456-426655440428';
UPDATE questionnaire.template_sections_questions SET display_order = 29 WHERE question_id = '123e4567-e89b-12d3-a456-426655440429';
UPDATE questionnaire.template_sections_questions SET display_order = 30 WHERE question_id = '123e4567-e89b-12d3-a456-426655440430';
UPDATE questionnaire.template_sections_questions SET display_order = 31 WHERE question_id = '123e4567-e89b-12d3-a456-426655440431';
UPDATE questionnaire.template_sections_questions SET display_order = 32 WHERE question_id = '123e4567-e89b-12d3-a456-426655440432';
UPDATE questionnaire.template_sections_questions SET display_order = 33 WHERE question_id = '123e4567-e89b-12d3-a456-426655440433';
UPDATE questionnaire.template_sections_questions SET display_order = 34 WHERE question_id = '123e4567-e89b-12d3-a456-426655440434';
UPDATE questionnaire.template_sections_questions SET display_order = 35 WHERE question_id = '123e4567-e89b-12d3-a456-426655440435';
UPDATE questionnaire.template_sections_questions SET display_order = 36 WHERE question_id = '123e4567-e89b-12d3-a456-426655440436';
UPDATE questionnaire.template_sections_questions SET display_order = 37 WHERE question_id = '123e4567-e89b-12d3-a456-426655440437';
UPDATE questionnaire.template_sections_questions SET display_order = 38 WHERE question_id = '123e4567-e89b-12d3-a456-426655440438';
UPDATE questionnaire.template_sections_questions SET display_order = 39 WHERE question_id = '123e4567-e89b-12d3-a456-426655440439';
UPDATE questionnaire.template_sections_questions SET display_order = 40 WHERE question_id = '123e4567-e89b-12d3-a456-426655440440';
UPDATE questionnaire.template_sections_questions SET display_order = 41 WHERE question_id = '123e4567-e89b-12d3-a456-426655440441';
UPDATE questionnaire.template_sections_questions SET display_order = 42 WHERE question_id = '123e4567-e89b-12d3-a456-426655440442';
UPDATE questionnaire.template_sections_questions SET display_order = 43 WHERE question_id = '123e4567-e89b-12d3-a456-426655440443';
UPDATE questionnaire.template_sections_questions SET display_order = 44 WHERE question_id = '123e4567-e89b-12d3-a456-426655440444';
UPDATE questionnaire.template_sections_questions SET display_order = 45 WHERE question_id = '123e4567-e89b-12d3-a456-426655440445';
UPDATE questionnaire.template_sections_questions SET display_order = 46 WHERE question_id = '123e4567-e89b-12d3-a456-426655440446';
UPDATE questionnaire.template_sections_questions SET display_order = 47 WHERE question_id = '123e4567-e89b-12d3-a456-426655440447';
UPDATE questionnaire.template_sections_questions SET display_order = 48 WHERE question_id = '123e4567-e89b-12d3-a456-426655440448';
UPDATE questionnaire.template_sections_questions SET display_order = 49 WHERE question_id = '123e4567-e89b-12d3-a456-426655440449';
UPDATE questionnaire.template_sections_questions SET display_order = 50 WHERE question_id = '123e4567-e89b-12d3-a456-426655440450';
UPDATE questionnaire.template_sections_questions SET display_order = 51 WHERE question_id = '123e4567-e89b-12d3-a456-426655440451';
UPDATE questionnaire.template_sections_questions SET display_order = 52 WHERE question_id = '123e4567-e89b-12d3-a456-426655440452';
UPDATE questionnaire.template_sections_questions SET display_order = 53 WHERE question_id = '123e4567-e89b-12d3-a456-426655440453';
UPDATE questionnaire.template_sections_questions SET display_order = 54 WHERE question_id = '123e4567-e89b-12d3-a456-426655440454';
UPDATE questionnaire.template_sections_questions SET display_order = 55 WHERE question_id = '123e4567-e89b-12d3-a456-426655440455';
UPDATE questionnaire.template_sections_questions SET display_order = 56 WHERE question_id = '123e4567-e89b-12d3-a456-426655440456';
UPDATE questionnaire.template_sections_questions SET display_order = 57 WHERE question_id = '123e4567-e89b-12d3-a456-426655440457';
UPDATE questionnaire.template_sections_questions SET display_order = 58 WHERE question_id = '123e4567-e89b-12d3-a456-426655440458';
UPDATE questionnaire.template_sections_questions SET display_order = 59 WHERE question_id = '123e4567-e89b-12d3-a456-426655440459';
UPDATE questionnaire.template_sections_questions SET display_order = 60 WHERE question_id = '123e4567-e89b-12d3-a456-426655440460';
UPDATE questionnaire.template_sections_questions SET display_order = 61 WHERE question_id = '123e4567-e89b-12d3-a456-426655440461';
UPDATE questionnaire.template_sections_questions SET display_order = 62 WHERE question_id = '123e4567-e89b-12d3-a456-426655440462';
UPDATE questionnaire.template_sections_questions SET display_order = 63 WHERE question_id = '123e4567-e89b-12d3-a456-426655440463';
UPDATE questionnaire.template_sections_questions SET display_order = 64 WHERE question_id = '123e4567-e89b-12d3-a456-426655440464';
UPDATE questionnaire.template_sections_questions SET display_order = 65 WHERE question_id = '123e4567-e89b-12d3-a456-426655440465';
UPDATE questionnaire.template_sections_questions SET display_order = 66 WHERE question_id = '123e4567-e89b-12d3-a456-426655440466';
UPDATE questionnaire.template_sections_questions SET display_order = 67 WHERE question_id = '123e4567-e89b-12d3-a456-426655440467';
UPDATE questionnaire.template_sections_questions SET display_order = 68 WHERE question_id = '123e4567-e89b-12d3-a456-426655440468';
UPDATE questionnaire.template_sections_questions SET display_order = 69 WHERE question_id = '123e4567-e89b-12d3-a456-426655440469';
UPDATE questionnaire.template_sections_questions SET display_order = 70 WHERE question_id = '123e4567-e89b-12d3-a456-426655440470';
UPDATE questionnaire.template_sections_questions SET display_order = 71 WHERE question_id = '123e4567-e89b-12d3-a456-426655440471';
UPDATE questionnaire.template_sections_questions SET display_order = 72 WHERE question_id = '123e4567-e89b-12d3-a456-426655440472';
UPDATE questionnaire.template_sections_questions SET display_order = 73 WHERE question_id = '123e4567-e89b-12d3-a456-426655440473';
UPDATE questionnaire.template_sections_questions SET display_order = 74 WHERE question_id = '123e4567-e89b-12d3-a456-426655440474';
UPDATE questionnaire.template_sections_questions SET display_order = 75 WHERE question_id = '123e4567-e89b-12d3-a456-426655440475';
UPDATE questionnaire.template_sections_questions SET display_order = 76 WHERE question_id = '123e4567-e89b-12d3-a456-426655440476';
UPDATE questionnaire.template_sections_questions SET display_order = 77 WHERE question_id = '123e4567-e89b-12d3-a456-426655440477';
UPDATE questionnaire.template_sections_questions SET display_order = 78 WHERE question_id = '123e4567-e89b-12d3-a456-426655440478';
UPDATE questionnaire.template_sections_questions SET display_order = 79 WHERE question_id = '123e4567-e89b-12d3-a456-426655440479';
UPDATE questionnaire.template_sections_questions SET display_order = 80 WHERE question_id = '123e4567-e89b-12d3-a456-426655440480';
UPDATE questionnaire.template_sections_questions SET display_order = 81 WHERE question_id = '123e4567-e89b-12d3-a456-426655440481';
UPDATE questionnaire.template_sections_questions SET display_order = 82 WHERE question_id = '123e4567-e89b-12d3-a456-426655440482';

-- UK AR Questionnaire Template (Tech section)

UPDATE questionnaire.template_sections
    SET display_order = 5
    WHERE id = 'e552d9e6-112f-4232-934b-860d4ff87f98';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '123e4567-e89b-12d3-a456-426655440501';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '123e4567-e89b-12d3-a456-426655440502';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '123e4567-e89b-12d3-a456-426655440503';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '123e4567-e89b-12d3-a456-426655440504';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '123e4567-e89b-12d3-a456-426655440505';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '123e4567-e89b-12d3-a456-426655440506';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '123e4567-e89b-12d3-a456-426655440507';
UPDATE questionnaire.template_sections_questions SET display_order = 8 WHERE question_id = '123e4567-e89b-12d3-a456-426655440508';
UPDATE questionnaire.template_sections_questions SET display_order = 9 WHERE question_id = '123e4567-e89b-12d3-a456-426655440509';
UPDATE questionnaire.template_sections_questions SET display_order = 10 WHERE question_id = '123e4567-e89b-12d3-a456-426655440510';
UPDATE questionnaire.template_sections_questions SET display_order = 11 WHERE question_id = '123e4567-e89b-12d3-a456-426655440511';
UPDATE questionnaire.template_sections_questions SET display_order = 12 WHERE question_id = '123e4567-e89b-12d3-a456-426655440512';
UPDATE questionnaire.template_sections_questions SET display_order = 13 WHERE question_id = '123e4567-e89b-12d3-a456-426655440513';
UPDATE questionnaire.template_sections_questions SET display_order = 14 WHERE question_id = '123e4567-e89b-12d3-a456-426655440514';
UPDATE questionnaire.template_sections_questions SET display_order = 15 WHERE question_id = '123e4567-e89b-12d3-a456-426655440515';
UPDATE questionnaire.template_sections_questions SET display_order = 16 WHERE question_id = '123e4567-e89b-12d3-a456-426655440516';
UPDATE questionnaire.template_sections_questions SET display_order = 17 WHERE question_id = '123e4567-e89b-12d3-a456-426655440517';
UPDATE questionnaire.template_sections_questions SET display_order = 18 WHERE question_id = '123e4567-e89b-12d3-a456-426655440518';
UPDATE questionnaire.template_sections_questions SET display_order = 19 WHERE question_id = '123e4567-e89b-12d3-a456-426655440519';
UPDATE questionnaire.template_sections_questions SET display_order = 20 WHERE question_id = '123e4567-e89b-12d3-a456-426655440520';
UPDATE questionnaire.template_sections_questions SET display_order = 21 WHERE question_id = '123e4567-e89b-12d3-a456-426655440521';
UPDATE questionnaire.template_sections_questions SET display_order = 22 WHERE question_id = '123e4567-e89b-12d3-a456-426655440522';
UPDATE questionnaire.template_sections_questions SET display_order = 23 WHERE question_id = '123e4567-e89b-12d3-a456-426655440523';
UPDATE questionnaire.template_sections_questions SET display_order = 24 WHERE question_id = '123e4567-e89b-12d3-a456-426655440524';
UPDATE questionnaire.template_sections_questions SET display_order = 25 WHERE question_id = '123e4567-e89b-12d3-a456-426655440525';
UPDATE questionnaire.template_sections_questions SET display_order = 26 WHERE question_id = '123e4567-e89b-12d3-a456-426655440526';
UPDATE questionnaire.template_sections_questions SET display_order = 27 WHERE question_id = '123e4567-e89b-12d3-a456-426655440527';
UPDATE questionnaire.template_sections_questions SET display_order = 28 WHERE question_id = '123e4567-e89b-12d3-a456-426655440528';
UPDATE questionnaire.template_sections_questions SET display_order = 29 WHERE question_id = '123e4567-e89b-12d3-a456-426655440529';
UPDATE questionnaire.template_sections_questions SET display_order = 30 WHERE question_id = '123e4567-e89b-12d3-a456-426655440530';
UPDATE questionnaire.template_sections_questions SET display_order = 31 WHERE question_id = '123e4567-e89b-12d3-a456-426655440531';
UPDATE questionnaire.template_sections_questions SET display_order = 32 WHERE question_id = '123e4567-e89b-12d3-a456-426655440532';
UPDATE questionnaire.template_sections_questions SET display_order = 33 WHERE question_id = '123e4567-e89b-12d3-a456-426655440533';
UPDATE questionnaire.template_sections_questions SET display_order = 34 WHERE question_id = '123e4567-e89b-12d3-a456-426655440534';
UPDATE questionnaire.template_sections_questions SET display_order = 35 WHERE question_id = '123e4567-e89b-12d3-a456-426655440535';
UPDATE questionnaire.template_sections_questions SET display_order = 36 WHERE question_id = '123e4567-e89b-12d3-a456-426655440536';

-- UK AR Questionnaire Template (Operations section)

UPDATE questionnaire.template_sections
    SET display_order = 6
    WHERE id = 'e552d9e6-112f-4232-934b-860d4ff87f99';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '123e4567-e89b-12d3-a456-426655440601';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '123e4567-e89b-12d3-a456-426655440602';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '123e4567-e89b-12d3-a456-426655440603';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '123e4567-e89b-12d3-a456-426655440604';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '123e4567-e89b-12d3-a456-426655440605';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '123e4567-e89b-12d3-a456-426655440606';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '123e4567-e89b-12d3-a456-426655440607';
UPDATE questionnaire.template_sections_questions SET display_order = 8 WHERE question_id = '123e4567-e89b-12d3-a456-426655440608';
UPDATE questionnaire.template_sections_questions SET display_order = 9 WHERE question_id = '123e4567-e89b-12d3-a456-426655440609';

-- UK AR Questionnaire Template (Claims section)

UPDATE questionnaire.template_sections
    SET display_order = 7
    WHERE id = 'e552d9e6-112f-4232-934b-860d4ff87e11';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '123e4567-e89b-12d3-a456-426655440701';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '123e4567-e89b-12d3-a456-426655440702';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '123e4567-e89b-12d3-a456-426655440703';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '123e4567-e89b-12d3-a456-426655440704';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '123e4567-e89b-12d3-a456-426655440705';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '123e4567-e89b-12d3-a456-426655440706';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '123e4567-e89b-12d3-a456-426655440707';
UPDATE questionnaire.template_sections_questions SET display_order = 8 WHERE question_id = '123e4567-e89b-12d3-a456-426655440708';
UPDATE questionnaire.template_sections_questions SET display_order = 9 WHERE question_id = '123e4567-e89b-12d3-a456-426655440709';
UPDATE questionnaire.template_sections_questions SET display_order = 10 WHERE question_id = '123e4567-e89b-12d3-a456-426655440710';
UPDATE questionnaire.template_sections_questions SET display_order = 11 WHERE question_id = '123e4567-e89b-12d3-a456-426655440711';
UPDATE questionnaire.template_sections_questions SET display_order = 12 WHERE question_id = '123e4567-e89b-12d3-a456-426655440712';
UPDATE questionnaire.template_sections_questions SET display_order = 13 WHERE question_id = '123e4567-e89b-12d3-a456-426655440713';
UPDATE questionnaire.template_sections_questions SET display_order = 14 WHERE question_id = '123e4567-e89b-12d3-a456-426655440714';
UPDATE questionnaire.template_sections_questions SET display_order = 15 WHERE question_id = '123e4567-e89b-12d3-a456-426655440715';
UPDATE questionnaire.template_sections_questions SET display_order = 16 WHERE question_id = '123e4567-e89b-12d3-a456-426655440716';
UPDATE questionnaire.template_sections_questions SET display_order = 17 WHERE question_id = '123e4567-e89b-12d3-a456-426655440717';
UPDATE questionnaire.template_sections_questions SET display_order = 18 WHERE question_id = '123e4567-e89b-12d3-a456-426655440718';
UPDATE questionnaire.template_sections_questions SET display_order = 19 WHERE question_id = '123e4567-e89b-12d3-a456-426655440719';
UPDATE questionnaire.template_sections_questions SET display_order = 20 WHERE question_id = '123e4567-e89b-12d3-a456-426655440720';
UPDATE questionnaire.template_sections_questions SET display_order = 21 WHERE question_id = '123e4567-e89b-12d3-a456-426655440721';
UPDATE questionnaire.template_sections_questions SET display_order = 22 WHERE question_id = '123e4567-e89b-12d3-a456-426655440722';

---------------

-- UK Non AR Questionnaire Template (General/Admin section)

UPDATE questionnaire.template_sections
    SET display_order = 1
    WHERE id = 'e552d9e6-112f-4232-934b-860d4ff19e99';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '123e4567-e89b-12d3-a456-426655440101';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '123e4567-e89b-12d3-a456-426655440102';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '123e4567-e89b-12d3-a456-426655440103';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '123e4567-e89b-12d3-a456-426655440104';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '123e4567-e89b-12d3-a456-426655440105';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '123e4567-e89b-12d3-a456-426655440106';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '123e4567-e89b-12d3-a456-426655440107';

-- UK Non AR Questionnaire Template (Finance section)

UPDATE questionnaire.template_sections
    SET display_order = 2
    WHERE id = 'e552d9e6-112f-4232-934b-860d4ff88c35';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '123e4567-e89b-12d3-a456-426655440201';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '123e4567-e89b-12d3-a456-426655440202';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '123e4567-e89b-12d3-a456-426655440203';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '123e4567-e89b-12d3-a456-426655440204';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '123e4567-e89b-12d3-a456-426655440205';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '123e4567-e89b-12d3-a456-426655440206';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '123e4567-e89b-12d3-a456-426655440207';
UPDATE questionnaire.template_sections_questions SET display_order = 8 WHERE question_id = '123e4567-e89b-12d3-a456-426655440208';
UPDATE questionnaire.template_sections_questions SET display_order = 9 WHERE question_id = '123e4567-e89b-12d3-a456-426655440209';
UPDATE questionnaire.template_sections_questions SET display_order = 10 WHERE question_id = '123e4567-e89b-12d3-a456-426655440210';
UPDATE questionnaire.template_sections_questions SET display_order = 11 WHERE question_id = '123e4567-e89b-12d3-a456-426655440211';
UPDATE questionnaire.template_sections_questions SET display_order = 12 WHERE question_id = '123e4567-e89b-12d3-a456-426655440212';
UPDATE questionnaire.template_sections_questions SET display_order = 13 WHERE question_id = '123e4567-e89b-12d3-a456-426655440213';
UPDATE questionnaire.template_sections_questions SET display_order = 14 WHERE question_id = '123e4567-e89b-12d3-a456-426655440214';
UPDATE questionnaire.template_sections_questions SET display_order = 15 WHERE question_id = '123e4567-e89b-12d3-a456-426655440215';
UPDATE questionnaire.template_sections_questions SET display_order = 16 WHERE question_id = '123e4567-e89b-12d3-a456-426655440216';
UPDATE questionnaire.template_sections_questions SET display_order = 17 WHERE question_id = '123e4567-e89b-12d3-a456-426655440217';
UPDATE questionnaire.template_sections_questions SET display_order = 18 WHERE question_id = '123e4567-e89b-12d3-a456-426655440218';
UPDATE questionnaire.template_sections_questions SET display_order = 19 WHERE question_id = '123e4567-e89b-12d3-a456-426655440219';
UPDATE questionnaire.template_sections_questions SET display_order = 20 WHERE question_id = '123e4567-e89b-12d3-a456-426655440220';
UPDATE questionnaire.template_sections_questions SET display_order = 21 WHERE question_id = '123e4567-e89b-12d3-a456-426655440221';
UPDATE questionnaire.template_sections_questions SET display_order = 22 WHERE question_id = '123e4567-e89b-12d3-a456-426655440222';
UPDATE questionnaire.template_sections_questions SET display_order = 23 WHERE question_id = '123e4567-e89b-12d3-a456-426655440223';
UPDATE questionnaire.template_sections_questions SET display_order = 24 WHERE question_id = '123e4567-e89b-12d3-a456-426655440224';
UPDATE questionnaire.template_sections_questions SET display_order = 25 WHERE question_id = '123e4567-e89b-12d3-a456-426655440225';
UPDATE questionnaire.template_sections_questions SET display_order = 26 WHERE question_id = '123e4567-e89b-12d3-a456-426655440226';
UPDATE questionnaire.template_sections_questions SET display_order = 27 WHERE question_id = '123e4567-e89b-12d3-a456-426655440227';

-- UK Non AR Questionnaire Template (Commercial section)

UPDATE questionnaire.template_sections
    SET display_order = 3
    WHERE id = 'e552d9e6-112f-4232-934b-860d4ff88e21';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '123e4567-e89b-12d3-a456-426655440301';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '123e4567-e89b-12d3-a456-426655440302';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '123e4567-e89b-12d3-a456-426655440303';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '123e4567-e89b-12d3-a456-426655440304';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '123e4567-e89b-12d3-a456-426655440305';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '123e4567-e89b-12d3-a456-426655440306';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '123e4567-e89b-12d3-a456-426655440307';
UPDATE questionnaire.template_sections_questions SET display_order = 8 WHERE question_id = '123e4567-e89b-12d3-a456-426655440308';
UPDATE questionnaire.template_sections_questions SET display_order = 9 WHERE question_id = '123e4567-e89b-12d3-a456-426655440309';
UPDATE questionnaire.template_sections_questions SET display_order = 10 WHERE question_id = '123e4567-e89b-12d3-a456-426655440310';
UPDATE questionnaire.template_sections_questions SET display_order = 11 WHERE question_id = '123e4567-e89b-12d3-a456-426655440311';

-- UK Non AR Questionnaire Template (Compliance section)

UPDATE questionnaire.template_sections
    SET display_order = 4
    WHERE id = 'e552d9e6-112f-4232-934b-860d4ff88c34';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '123e4567-e89b-12d3-a456-426655440401';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '123e4567-e89b-12d3-a456-426655440402';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '123e4567-e89b-12d3-a456-426655440403';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '123e4567-e89b-12d3-a456-426655440404';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '123e4567-e89b-12d3-a456-426655440405';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '123e4567-e89b-12d3-a456-426655440406';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '123e4567-e89b-12d3-a456-426655440407';
UPDATE questionnaire.template_sections_questions SET display_order = 8 WHERE question_id = '123e4567-e89b-12d3-a456-426655440408';
UPDATE questionnaire.template_sections_questions SET display_order = 9 WHERE question_id = '123e4567-e89b-12d3-a456-426655440409';
UPDATE questionnaire.template_sections_questions SET display_order = 10 WHERE question_id = '123e4567-e89b-12d3-a456-426655440410';
UPDATE questionnaire.template_sections_questions SET display_order = 11 WHERE question_id = '123e4567-e89b-12d3-a456-426655440411';
UPDATE questionnaire.template_sections_questions SET display_order = 12 WHERE question_id = '123e4567-e89b-12d3-a456-426655440412';
UPDATE questionnaire.template_sections_questions SET display_order = 13 WHERE question_id = '123e4567-e89b-12d3-a456-426655440413';
UPDATE questionnaire.template_sections_questions SET display_order = 14 WHERE question_id = '123e4567-e89b-12d3-a456-426655440414';
UPDATE questionnaire.template_sections_questions SET display_order = 15 WHERE question_id = '123e4567-e89b-12d3-a456-426655440415';
UPDATE questionnaire.template_sections_questions SET display_order = 16 WHERE question_id = '123e4567-e89b-12d3-a456-426655440416';
UPDATE questionnaire.template_sections_questions SET display_order = 17 WHERE question_id = '123e4567-e89b-12d3-a456-426655440417';
UPDATE questionnaire.template_sections_questions SET display_order = 18 WHERE question_id = '123e4567-e89b-12d3-a456-426655440418';
UPDATE questionnaire.template_sections_questions SET display_order = 19 WHERE question_id = '123e4567-e89b-12d3-a456-426655440419';
UPDATE questionnaire.template_sections_questions SET display_order = 20 WHERE question_id = '123e4567-e89b-12d3-a456-426655440420';
UPDATE questionnaire.template_sections_questions SET display_order = 21 WHERE question_id = '123e4567-e89b-12d3-a456-426655440421';
UPDATE questionnaire.template_sections_questions SET display_order = 22 WHERE question_id = '123e4567-e89b-12d3-a456-426655440422';
UPDATE questionnaire.template_sections_questions SET display_order = 23 WHERE question_id = '123e4567-e89b-12d3-a456-426655440423';
UPDATE questionnaire.template_sections_questions SET display_order = 24 WHERE question_id = '123e4567-e89b-12d3-a456-426655440424';
UPDATE questionnaire.template_sections_questions SET display_order = 25 WHERE question_id = '123e4567-e89b-12d3-a456-426655440425';
UPDATE questionnaire.template_sections_questions SET display_order = 26 WHERE question_id = '123e4567-e89b-12d3-a456-426655440426';
UPDATE questionnaire.template_sections_questions SET display_order = 27 WHERE question_id = '123e4567-e89b-12d3-a456-426655440427';
UPDATE questionnaire.template_sections_questions SET display_order = 28 WHERE question_id = '123e4567-e89b-12d3-a456-426655440428';
UPDATE questionnaire.template_sections_questions SET display_order = 29 WHERE question_id = '123e4567-e89b-12d3-a456-426655440429';
UPDATE questionnaire.template_sections_questions SET display_order = 30 WHERE question_id = '123e4567-e89b-12d3-a456-426655440430';
UPDATE questionnaire.template_sections_questions SET display_order = 31 WHERE question_id = '123e4567-e89b-12d3-a456-426655440431';
UPDATE questionnaire.template_sections_questions SET display_order = 32 WHERE question_id = '123e4567-e89b-12d3-a456-426655440432';
UPDATE questionnaire.template_sections_questions SET display_order = 33 WHERE question_id = '123e4567-e89b-12d3-a456-426655440433';
UPDATE questionnaire.template_sections_questions SET display_order = 34 WHERE question_id = '123e4567-e89b-12d3-a456-426655440434';
UPDATE questionnaire.template_sections_questions SET display_order = 35 WHERE question_id = '123e4567-e89b-12d3-a456-426655440435';
UPDATE questionnaire.template_sections_questions SET display_order = 36 WHERE question_id = '123e4567-e89b-12d3-a456-426655440436';
UPDATE questionnaire.template_sections_questions SET display_order = 37 WHERE question_id = '123e4567-e89b-12d3-a456-426655440437';
UPDATE questionnaire.template_sections_questions SET display_order = 38 WHERE question_id = '123e4567-e89b-12d3-a456-426655440438';
UPDATE questionnaire.template_sections_questions SET display_order = 39 WHERE question_id = '123e4567-e89b-12d3-a456-426655440439';
UPDATE questionnaire.template_sections_questions SET display_order = 40 WHERE question_id = '123e4567-e89b-12d3-a456-426655440440';
UPDATE questionnaire.template_sections_questions SET display_order = 41 WHERE question_id = '123e4567-e89b-12d3-a456-426655440441';
UPDATE questionnaire.template_sections_questions SET display_order = 42 WHERE question_id = '123e4567-e89b-12d3-a456-426655440442';
UPDATE questionnaire.template_sections_questions SET display_order = 43 WHERE question_id = '123e4567-e89b-12d3-a456-426655440443';
UPDATE questionnaire.template_sections_questions SET display_order = 44 WHERE question_id = '123e4567-e89b-12d3-a456-426655440444';
UPDATE questionnaire.template_sections_questions SET display_order = 45 WHERE question_id = '123e4567-e89b-12d3-a456-426655440445';
UPDATE questionnaire.template_sections_questions SET display_order = 46 WHERE question_id = '123e4567-e89b-12d3-a456-426655440446';
UPDATE questionnaire.template_sections_questions SET display_order = 47 WHERE question_id = '123e4567-e89b-12d3-a456-426655440447';
UPDATE questionnaire.template_sections_questions SET display_order = 48 WHERE question_id = '123e4567-e89b-12d3-a456-426655440448';
UPDATE questionnaire.template_sections_questions SET display_order = 49 WHERE question_id = '123e4567-e89b-12d3-a456-426655440449';
UPDATE questionnaire.template_sections_questions SET display_order = 50 WHERE question_id = '123e4567-e89b-12d3-a456-426655440450';
UPDATE questionnaire.template_sections_questions SET display_order = 51 WHERE question_id = '123e4567-e89b-12d3-a456-426655440451';
UPDATE questionnaire.template_sections_questions SET display_order = 52 WHERE question_id = '123e4567-e89b-12d3-a456-426655440452';
UPDATE questionnaire.template_sections_questions SET display_order = 53 WHERE question_id = '123e4567-e89b-12d3-a456-426655440453';
UPDATE questionnaire.template_sections_questions SET display_order = 54 WHERE question_id = '123e4567-e89b-12d3-a456-426655440454';
UPDATE questionnaire.template_sections_questions SET display_order = 55 WHERE question_id = '123e4567-e89b-12d3-a456-426655440455';
UPDATE questionnaire.template_sections_questions SET display_order = 56 WHERE question_id = '123e4567-e89b-12d3-a456-426655440456';
UPDATE questionnaire.template_sections_questions SET display_order = 57 WHERE question_id = '123e4567-e89b-12d3-a456-426655440457';
UPDATE questionnaire.template_sections_questions SET display_order = 58 WHERE question_id = '123e4567-e89b-12d3-a456-426655440458';
UPDATE questionnaire.template_sections_questions SET display_order = 59 WHERE question_id = '123e4567-e89b-12d3-a456-426655440459';
UPDATE questionnaire.template_sections_questions SET display_order = 60 WHERE question_id = '123e4567-e89b-12d3-a456-426655440460';
UPDATE questionnaire.template_sections_questions SET display_order = 61 WHERE question_id = '123e4567-e89b-12d3-a456-426655440461';
UPDATE questionnaire.template_sections_questions SET display_order = 62 WHERE question_id = '123e4567-e89b-12d3-a456-426655440462';
UPDATE questionnaire.template_sections_questions SET display_order = 63 WHERE question_id = '123e4567-e89b-12d3-a456-426655440463';
UPDATE questionnaire.template_sections_questions SET display_order = 64 WHERE question_id = '123e4567-e89b-12d3-a456-426655440464';
UPDATE questionnaire.template_sections_questions SET display_order = 65 WHERE question_id = '123e4567-e89b-12d3-a456-426655440465';
UPDATE questionnaire.template_sections_questions SET display_order = 66 WHERE question_id = '123e4567-e89b-12d3-a456-426655440466';
UPDATE questionnaire.template_sections_questions SET display_order = 67 WHERE question_id = '123e4567-e89b-12d3-a456-426655440467';
UPDATE questionnaire.template_sections_questions SET display_order = 68 WHERE question_id = '123e4567-e89b-12d3-a456-426655440468';
UPDATE questionnaire.template_sections_questions SET display_order = 69 WHERE question_id = '123e4567-e89b-12d3-a456-426655440469';
UPDATE questionnaire.template_sections_questions SET display_order = 70 WHERE question_id = '123e4567-e89b-12d3-a456-426655440470';
UPDATE questionnaire.template_sections_questions SET display_order = 71 WHERE question_id = '123e4567-e89b-12d3-a456-426655440471';
UPDATE questionnaire.template_sections_questions SET display_order = 72 WHERE question_id = '123e4567-e89b-12d3-a456-426655440472';
UPDATE questionnaire.template_sections_questions SET display_order = 73 WHERE question_id = '123e4567-e89b-12d3-a456-426655440474';
UPDATE questionnaire.template_sections_questions SET display_order = 74 WHERE question_id = '123e4567-e89b-12d3-a456-426655440475';
UPDATE questionnaire.template_sections_questions SET display_order = 75 WHERE question_id = '123e4567-e89b-12d3-a456-426655440476';
UPDATE questionnaire.template_sections_questions SET display_order = 76 WHERE question_id = '123e4567-e89b-12d3-a456-426655440477';
UPDATE questionnaire.template_sections_questions SET display_order = 77 WHERE question_id = '123e4567-e89b-12d3-a456-426655440478';
UPDATE questionnaire.template_sections_questions SET display_order = 78 WHERE question_id = '123e4567-e89b-12d3-a456-426655440479';
UPDATE questionnaire.template_sections_questions SET display_order = 79 WHERE question_id = '123e4567-e89b-12d3-a456-426655440480';
UPDATE questionnaire.template_sections_questions SET display_order = 80 WHERE question_id = '123e4567-e89b-12d3-a456-426655440481';
UPDATE questionnaire.template_sections_questions SET display_order = 81 WHERE question_id = '123e4567-e89b-12d3-a456-426655440482';

-- UK Non AR Questionnaire Template (Tech section)

UPDATE questionnaire.template_sections
    SET display_order = 5
    WHERE id = 'e552d9e6-112f-4232-934b-860d4ff88e55';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '123e4567-e89b-12d3-a456-426655440501';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '123e4567-e89b-12d3-a456-426655440502';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '123e4567-e89b-12d3-a456-426655440503';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '123e4567-e89b-12d3-a456-426655440504';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '123e4567-e89b-12d3-a456-426655440505';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '123e4567-e89b-12d3-a456-426655440506';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '123e4567-e89b-12d3-a456-426655440507';
UPDATE questionnaire.template_sections_questions SET display_order = 8 WHERE question_id = '123e4567-e89b-12d3-a456-426655440508';
UPDATE questionnaire.template_sections_questions SET display_order = 9 WHERE question_id = '123e4567-e89b-12d3-a456-426655440509';
UPDATE questionnaire.template_sections_questions SET display_order = 10 WHERE question_id = '123e4567-e89b-12d3-a456-426655440510';
UPDATE questionnaire.template_sections_questions SET display_order = 11 WHERE question_id = '123e4567-e89b-12d3-a456-426655440511';
UPDATE questionnaire.template_sections_questions SET display_order = 12 WHERE question_id = '123e4567-e89b-12d3-a456-426655440512';
UPDATE questionnaire.template_sections_questions SET display_order = 13 WHERE question_id = '123e4567-e89b-12d3-a456-426655440513';
UPDATE questionnaire.template_sections_questions SET display_order = 14 WHERE question_id = '123e4567-e89b-12d3-a456-426655440514';
UPDATE questionnaire.template_sections_questions SET display_order = 15 WHERE question_id = '123e4567-e89b-12d3-a456-426655440515';
UPDATE questionnaire.template_sections_questions SET display_order = 16 WHERE question_id = '123e4567-e89b-12d3-a456-426655440516';
UPDATE questionnaire.template_sections_questions SET display_order = 17 WHERE question_id = '123e4567-e89b-12d3-a456-426655440517';
UPDATE questionnaire.template_sections_questions SET display_order = 18 WHERE question_id = '123e4567-e89b-12d3-a456-426655440518';
UPDATE questionnaire.template_sections_questions SET display_order = 19 WHERE question_id = '123e4567-e89b-12d3-a456-426655440519';
UPDATE questionnaire.template_sections_questions SET display_order = 20 WHERE question_id = '123e4567-e89b-12d3-a456-426655440520';
UPDATE questionnaire.template_sections_questions SET display_order = 21 WHERE question_id = '123e4567-e89b-12d3-a456-426655440521';
UPDATE questionnaire.template_sections_questions SET display_order = 22 WHERE question_id = '123e4567-e89b-12d3-a456-426655440522';
UPDATE questionnaire.template_sections_questions SET display_order = 23 WHERE question_id = '123e4567-e89b-12d3-a456-426655440523';
UPDATE questionnaire.template_sections_questions SET display_order = 24 WHERE question_id = '123e4567-e89b-12d3-a456-426655440524';
UPDATE questionnaire.template_sections_questions SET display_order = 25 WHERE question_id = '123e4567-e89b-12d3-a456-426655440525';
UPDATE questionnaire.template_sections_questions SET display_order = 26 WHERE question_id = '123e4567-e89b-12d3-a456-426655440526';
UPDATE questionnaire.template_sections_questions SET display_order = 27 WHERE question_id = '123e4567-e89b-12d3-a456-426655440527';
UPDATE questionnaire.template_sections_questions SET display_order = 28 WHERE question_id = '123e4567-e89b-12d3-a456-426655440528';
UPDATE questionnaire.template_sections_questions SET display_order = 29 WHERE question_id = '123e4567-e89b-12d3-a456-426655440529';
UPDATE questionnaire.template_sections_questions SET display_order = 30 WHERE question_id = '123e4567-e89b-12d3-a456-426655440530';
UPDATE questionnaire.template_sections_questions SET display_order = 31 WHERE question_id = '123e4567-e89b-12d3-a456-426655440531';
UPDATE questionnaire.template_sections_questions SET display_order = 32 WHERE question_id = '123e4567-e89b-12d3-a456-426655440532';
UPDATE questionnaire.template_sections_questions SET display_order = 33 WHERE question_id = '123e4567-e89b-12d3-a456-426655440533';
UPDATE questionnaire.template_sections_questions SET display_order = 34 WHERE question_id = '123e4567-e89b-12d3-a456-426655440534';
UPDATE questionnaire.template_sections_questions SET display_order = 35 WHERE question_id = '123e4567-e89b-12d3-a456-426655440535';
UPDATE questionnaire.template_sections_questions SET display_order = 36 WHERE question_id = '123e4567-e89b-12d3-a456-426655440536';

-- UK Non AR Questionnaire Template (Operations section)

UPDATE questionnaire.template_sections
    SET display_order = 6
    WHERE id = 'e552d9e6-112f-4232-934b-860d4ff88b55';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '123e4567-e89b-12d3-a456-426655440601';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '123e4567-e89b-12d3-a456-426655440602';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '123e4567-e89b-12d3-a456-426655440603';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '123e4567-e89b-12d3-a456-426655440604';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '123e4567-e89b-12d3-a456-426655440605';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '123e4567-e89b-12d3-a456-426655440606';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '123e4567-e89b-12d3-a456-426655440607';
UPDATE questionnaire.template_sections_questions SET display_order = 8 WHERE question_id = '123e4567-e89b-12d3-a456-426655440608';
UPDATE questionnaire.template_sections_questions SET display_order = 9 WHERE question_id = '123e4567-e89b-12d3-a456-426655440609';

-- UK Non AR Questionnaire Template (Claims section)

UPDATE questionnaire.template_sections
    SET display_order = 7
    WHERE id = 'e552d9e6-112f-4232-934b-860d4ff88c24';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '123e4567-e89b-12d3-a456-426655440701';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '123e4567-e89b-12d3-a456-426655440702';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '123e4567-e89b-12d3-a456-426655440703';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '123e4567-e89b-12d3-a456-426655440704';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '123e4567-e89b-12d3-a456-426655440705';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '123e4567-e89b-12d3-a456-426655440706';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '123e4567-e89b-12d3-a456-426655440707';
UPDATE questionnaire.template_sections_questions SET display_order = 8 WHERE question_id = '123e4567-e89b-12d3-a456-426655440708';
UPDATE questionnaire.template_sections_questions SET display_order = 9 WHERE question_id = '123e4567-e89b-12d3-a456-426655440709';
UPDATE questionnaire.template_sections_questions SET display_order = 10 WHERE question_id = '123e4567-e89b-12d3-a456-426655440710';
UPDATE questionnaire.template_sections_questions SET display_order = 11 WHERE question_id = '123e4567-e89b-12d3-a456-426655440711';
UPDATE questionnaire.template_sections_questions SET display_order = 12 WHERE question_id = '123e4567-e89b-12d3-a456-426655440712';
UPDATE questionnaire.template_sections_questions SET display_order = 13 WHERE question_id = '123e4567-e89b-12d3-a456-426655440713';
UPDATE questionnaire.template_sections_questions SET display_order = 14 WHERE question_id = '123e4567-e89b-12d3-a456-426655440714';
UPDATE questionnaire.template_sections_questions SET display_order = 15 WHERE question_id = '123e4567-e89b-12d3-a456-426655440715';
UPDATE questionnaire.template_sections_questions SET display_order = 16 WHERE question_id = '123e4567-e89b-12d3-a456-426655440716';
UPDATE questionnaire.template_sections_questions SET display_order = 17 WHERE question_id = '123e4567-e89b-12d3-a456-426655440717';
UPDATE questionnaire.template_sections_questions SET display_order = 18 WHERE question_id = '123e4567-e89b-12d3-a456-426655440718';
UPDATE questionnaire.template_sections_questions SET display_order = 19 WHERE question_id = '123e4567-e89b-12d3-a456-426655440719';
UPDATE questionnaire.template_sections_questions SET display_order = 20 WHERE question_id = '123e4567-e89b-12d3-a456-426655440720';
UPDATE questionnaire.template_sections_questions SET display_order = 21 WHERE question_id = '123e4567-e89b-12d3-a456-426655440721';
UPDATE questionnaire.template_sections_questions SET display_order = 22 WHERE question_id = '123e4567-e89b-12d3-a456-426655440722';

--------------------------------------

-- US Questionnaire Template (General/Admin section)

UPDATE questionnaire.template_sections
    SET display_order = 1
    WHERE id = 'e552d8e6-112f-4232-934b-860d4ff19e99';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '123e4567-e89b-12d3-a456-426655440101';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '123e4567-e89b-12d3-a456-426655440102';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '123e4567-e89b-12d3-a456-426655440103';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '123e4567-e89b-12d3-a456-426655440104';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '123e4567-e89b-12d3-a456-426655440105';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '123e4567-e89b-12d3-a456-426655440106';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '123e4567-e89b-12d3-a456-426655440107';

-- US Questionnaire Template (IT System Compliance section)

UPDATE questionnaire.template_sections
    SET display_order = 2
    WHERE id = 'e552d8e6-112f-4232-934b-860d4ff19a22';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '113e4567-e89b-12d3-a456-426655440201';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '113e4567-e89b-12d3-a456-426655440202';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '113e4567-e89b-12d3-a456-426655440203';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '113e4567-e89b-12d3-a456-426655440204';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '113e4567-e89b-12d3-a456-426655440205';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '113e4567-e89b-12d3-a456-426655440206';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '113e4567-e89b-12d3-a456-426655440207';
UPDATE questionnaire.template_sections_questions SET display_order = 8 WHERE question_id = '113e4567-e89b-12d3-a456-426655440208';
UPDATE questionnaire.template_sections_questions SET display_order = 9 WHERE question_id = '113e4567-e89b-12d3-a456-426655440209';
UPDATE questionnaire.template_sections_questions SET display_order = 10 WHERE question_id = '113e4567-e89b-12d3-a456-426655440210';
UPDATE questionnaire.template_sections_questions SET display_order = 11 WHERE question_id = '113e4567-e89b-12d3-a456-426655440211';
UPDATE questionnaire.template_sections_questions SET display_order = 12 WHERE question_id = '113e4567-e89b-12d3-a456-426655440212';
UPDATE questionnaire.template_sections_questions SET display_order = 13 WHERE question_id = '113e4567-e89b-12d3-a456-426655440213';
UPDATE questionnaire.template_sections_questions SET display_order = 14 WHERE question_id = '113e4567-e89b-12d3-a456-426655440214';
UPDATE questionnaire.template_sections_questions SET display_order = 15 WHERE question_id = '113e4567-e89b-12d3-a456-426655440215';
UPDATE questionnaire.template_sections_questions SET display_order = 16 WHERE question_id = '113e4567-e89b-12d3-a456-426655440216';
UPDATE questionnaire.template_sections_questions SET display_order = 17 WHERE question_id = '113e4567-e89b-12d3-a456-426655440217';
UPDATE questionnaire.template_sections_questions SET display_order = 18 WHERE question_id = '113e4567-e89b-12d3-a456-426655440218';
UPDATE questionnaire.template_sections_questions SET display_order = 19 WHERE question_id = '113e4567-e89b-12d3-a456-426655440219';
UPDATE questionnaire.template_sections_questions SET display_order = 20 WHERE question_id = '113e4567-e89b-12d3-a456-426655440220';
UPDATE questionnaire.template_sections_questions SET display_order = 21 WHERE question_id = '113e4567-e89b-12d3-a456-426655440221';
UPDATE questionnaire.template_sections_questions SET display_order = 22 WHERE question_id = '113e4567-e89b-12d3-a456-426655440222';
UPDATE questionnaire.template_sections_questions SET display_order = 23 WHERE question_id = '113e4567-e89b-12d3-a456-426655440223';
UPDATE questionnaire.template_sections_questions SET display_order = 24 WHERE question_id = '113e4567-e89b-12d3-a456-426655440224';
UPDATE questionnaire.template_sections_questions SET display_order = 25 WHERE question_id = '113e4567-e89b-12d3-a456-426655440225';
UPDATE questionnaire.template_sections_questions SET display_order = 26 WHERE question_id = '113e4567-e89b-12d3-a456-426655440226';
UPDATE questionnaire.template_sections_questions SET display_order = 27 WHERE question_id = '113e4567-e89b-12d3-a456-426655440227';
UPDATE questionnaire.template_sections_questions SET display_order = 28 WHERE question_id = '113e4567-e89b-12d3-a456-426655440228';
UPDATE questionnaire.template_sections_questions SET display_order = 29 WHERE question_id = '113e4567-e89b-12d3-a456-426655440229';
UPDATE questionnaire.template_sections_questions SET display_order = 30 WHERE question_id = '113e4567-e89b-12d3-a456-426655440230';
UPDATE questionnaire.template_sections_questions SET display_order = 31 WHERE question_id = '113e4567-e89b-12d3-a456-426655440231';
UPDATE questionnaire.template_sections_questions SET display_order = 32 WHERE question_id = '113e4567-e89b-12d3-a456-426655440232';
UPDATE questionnaire.template_sections_questions SET display_order = 33 WHERE question_id = '113e4567-e89b-12d3-a456-426655440233';
UPDATE questionnaire.template_sections_questions SET display_order = 34 WHERE question_id = '113e4567-e89b-12d3-a456-426655440234';
UPDATE questionnaire.template_sections_questions SET display_order = 35 WHERE question_id = '113e4567-e89b-12d3-a456-426655440235';
UPDATE questionnaire.template_sections_questions SET display_order = 36 WHERE question_id = '113e4567-e89b-12d3-a456-426655440236';
UPDATE questionnaire.template_sections_questions SET display_order = 37 WHERE question_id = '113e4567-e89b-12d3-a456-426655440237';
UPDATE questionnaire.template_sections_questions SET display_order = 38 WHERE question_id = '113e4567-e89b-12d3-a456-426655440238';
UPDATE questionnaire.template_sections_questions SET display_order = 39 WHERE question_id = '113e4567-e89b-12d3-a456-426655440239';
UPDATE questionnaire.template_sections_questions SET display_order = 40 WHERE question_id = '113e4567-e89b-12d3-a456-426655440240';
UPDATE questionnaire.template_sections_questions SET display_order = 41 WHERE question_id = '113e4567-e89b-12d3-a456-426655440241';
UPDATE questionnaire.template_sections_questions SET display_order = 42 WHERE question_id = '113e4567-e89b-12d3-a456-426655440242';
UPDATE questionnaire.template_sections_questions SET display_order = 43 WHERE question_id = '113e4567-e89b-12d3-a456-426655440243';
UPDATE questionnaire.template_sections_questions SET display_order = 44 WHERE question_id = '113e4567-e89b-12d3-a456-426655440244';
UPDATE questionnaire.template_sections_questions SET display_order = 45 WHERE question_id = '113e4567-e89b-12d3-a456-426655440245';
UPDATE questionnaire.template_sections_questions SET display_order = 46 WHERE question_id = '113e4567-e89b-12d3-a456-426655440246';
UPDATE questionnaire.template_sections_questions SET display_order = 47 WHERE question_id = '113e4567-e89b-12d3-a456-426655440247';
UPDATE questionnaire.template_sections_questions SET display_order = 48 WHERE question_id = '113e4567-e89b-12d3-a456-426655440248';
UPDATE questionnaire.template_sections_questions SET display_order = 49 WHERE question_id = '113e4567-e89b-12d3-a456-426655440249';
UPDATE questionnaire.template_sections_questions SET display_order = 50 WHERE question_id = '113e4567-e89b-12d3-a456-426655440250';
UPDATE questionnaire.template_sections_questions SET display_order = 51 WHERE question_id = '113e4567-e89b-12d3-a456-426655440251';
UPDATE questionnaire.template_sections_questions SET display_order = 52 WHERE question_id = '113e4567-e89b-12d3-a456-426655440252';
UPDATE questionnaire.template_sections_questions SET display_order = 53 WHERE question_id = '113e4567-e89b-12d3-a456-426655440253';
UPDATE questionnaire.template_sections_questions SET display_order = 54 WHERE question_id = '113e4567-e89b-12d3-a456-426655440254';
UPDATE questionnaire.template_sections_questions SET display_order = 55 WHERE question_id = '113e4567-e89b-12d3-a456-426655440255';

-- US Questionnaire Template (Regulatory/Contractual Compliance section)

UPDATE questionnaire.template_sections
    SET display_order = 3
    WHERE id = 'e552d8e6-112f-4232-934b-860d4ff19a23';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '113e4567-e89b-12d3-a456-426655440301';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '113e4567-e89b-12d3-a456-426655440302';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '113e4567-e89b-12d3-a456-426655440303';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '113e4567-e89b-12d3-a456-426655440304';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '113e4567-e89b-12d3-a456-426655440305';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '113e4567-e89b-12d3-a456-426655440306';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '113e4567-e89b-12d3-a456-426655440307';
UPDATE questionnaire.template_sections_questions SET display_order = 8 WHERE question_id = '113e4567-e89b-12d3-a456-426655440308';
UPDATE questionnaire.template_sections_questions SET display_order = 9 WHERE question_id = '113e4567-e89b-12d3-a456-426655440309';
UPDATE questionnaire.template_sections_questions SET display_order = 10 WHERE question_id = '113e4567-e89b-12d3-a456-426655440310';
UPDATE questionnaire.template_sections_questions SET display_order = 11 WHERE question_id = '113e4567-e89b-12d3-a456-426655440311';
UPDATE questionnaire.template_sections_questions SET display_order = 12 WHERE question_id = '113e4567-e89b-12d3-a456-426655440312';
UPDATE questionnaire.template_sections_questions SET display_order = 13 WHERE question_id = '113e4567-e89b-12d3-a456-426655440313';
UPDATE questionnaire.template_sections_questions SET display_order = 14 WHERE question_id = '113e4567-e89b-12d3-a456-426655440314';
UPDATE questionnaire.template_sections_questions SET display_order = 15 WHERE question_id = '113e4567-e89b-12d3-a456-426655440315';
UPDATE questionnaire.template_sections_questions SET display_order = 16 WHERE question_id = '113e4567-e89b-12d3-a456-426655440316';
UPDATE questionnaire.template_sections_questions SET display_order = 17 WHERE question_id = '113e4567-e89b-12d3-a456-426655440317';
UPDATE questionnaire.template_sections_questions SET display_order = 18 WHERE question_id = '113e4567-e89b-12d3-a456-426655440318';
UPDATE questionnaire.template_sections_questions SET display_order = 19 WHERE question_id = '113e4567-e89b-12d3-a456-426655440319';
UPDATE questionnaire.template_sections_questions SET display_order = 20 WHERE question_id = '113e4567-e89b-12d3-a456-426655440320';
UPDATE questionnaire.template_sections_questions SET display_order = 21 WHERE question_id = '113e4567-e89b-12d3-a456-426655440321';
UPDATE questionnaire.template_sections_questions SET display_order = 22 WHERE question_id = '113e4567-e89b-12d3-a456-426655440322';
UPDATE questionnaire.template_sections_questions SET display_order = 23 WHERE question_id = '113e4567-e89b-12d3-a456-426655440323';

-- US Questionnaire Template (Financial Compliance Compliance section)

UPDATE questionnaire.template_sections
    SET display_order = 4
    WHERE id = 'e552d8e6-112f-4232-934b-860d4ff19a24';

UPDATE questionnaire.template_sections_questions SET display_order = 1 WHERE question_id = '113e4567-e89b-12d3-a456-426655440401';
UPDATE questionnaire.template_sections_questions SET display_order = 2 WHERE question_id = '113e4567-e89b-12d3-a456-426655440402';
UPDATE questionnaire.template_sections_questions SET display_order = 3 WHERE question_id = '113e4567-e89b-12d3-a456-426655440403';
UPDATE questionnaire.template_sections_questions SET display_order = 4 WHERE question_id = '113e4567-e89b-12d3-a456-426655440404';
UPDATE questionnaire.template_sections_questions SET display_order = 5 WHERE question_id = '113e4567-e89b-12d3-a456-426655440405';
UPDATE questionnaire.template_sections_questions SET display_order = 6 WHERE question_id = '113e4567-e89b-12d3-a456-426655440406';
UPDATE questionnaire.template_sections_questions SET display_order = 7 WHERE question_id = '113e4567-e89b-12d3-a456-426655440407';
UPDATE questionnaire.template_sections_questions SET display_order = 8 WHERE question_id = '113e4567-e89b-12d3-a456-426655440408';
UPDATE questionnaire.template_sections_questions SET display_order = 9 WHERE question_id = '113e4567-e89b-12d3-a456-426655440409';
UPDATE questionnaire.template_sections_questions SET display_order = 10 WHERE question_id = '113e4567-e89b-12d3-a456-426655440410';
UPDATE questionnaire.template_sections_questions SET display_order = 11 WHERE question_id = '113e4567-e89b-12d3-a456-426655440411';
UPDATE questionnaire.template_sections_questions SET display_order = 12 WHERE question_id = '113e4567-e89b-12d3-a456-426655440412';
UPDATE questionnaire.template_sections_questions SET display_order = 13 WHERE question_id = '113e4567-e89b-12d3-a456-426655440413';
UPDATE questionnaire.template_sections_questions SET display_order = 14 WHERE question_id = '113e4567-e89b-12d3-a456-426655440414';
UPDATE questionnaire.template_sections_questions SET display_order = 15 WHERE question_id = '113e4567-e89b-12d3-a456-426655440415';
UPDATE questionnaire.template_sections_questions SET display_order = 16 WHERE question_id = '113e4567-e89b-12d3-a456-426655440416';
UPDATE questionnaire.template_sections_questions SET display_order = 17 WHERE question_id = '113e4567-e89b-12d3-a456-426655440417';
UPDATE questionnaire.template_sections_questions SET display_order = 18 WHERE question_id = '113e4567-e89b-12d3-a456-426655440418';
UPDATE questionnaire.template_sections_questions SET display_order = 19 WHERE question_id = '113e4567-e89b-12d3-a456-426655440419';
UPDATE questionnaire.template_sections_questions SET display_order = 20 WHERE question_id = '113e4567-e89b-12d3-a456-426655440420';
UPDATE questionnaire.template_sections_questions SET display_order = 21 WHERE question_id = '113e4567-e89b-12d3-a456-426655440421';
UPDATE questionnaire.template_sections_questions SET display_order = 22 WHERE question_id = '113e4567-e89b-12d3-a456-426655440422';
UPDATE questionnaire.template_sections_questions SET display_order = 23 WHERE question_id = '113e4567-e89b-12d3-a456-426655440423';
UPDATE questionnaire.template_sections_questions SET display_order = 24 WHERE question_id = '113e4567-e89b-12d3-a456-426655440424';
UPDATE questionnaire.template_sections_questions SET display_order = 25 WHERE question_id = '113e4567-e89b-12d3-a456-426655440425';
UPDATE questionnaire.template_sections_questions SET display_order = 26 WHERE question_id = '113e4567-e89b-12d3-a456-426655440426';
UPDATE questionnaire.template_sections_questions SET display_order = 27 WHERE question_id = '113e4567-e89b-12d3-a456-426655440427';

------------------------------------

ALTER TABLE questionnaire.template_sections
    ALTER COLUMN display_order DROP DEFAULT;

ALTER TABLE questionnaire.template_sections_questions
    ALTER COLUMN display_order DROP DEFAULT;

ALTER TABLE questionnaire.sections
    ALTER COLUMN display_order DROP DEFAULT;

ALTER TABLE questionnaire.sections_questions
    ALTER COLUMN display_order DROP DEFAULT;

COMMIT;
