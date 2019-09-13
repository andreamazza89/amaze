package com.mrdigitalpartners.oprah.web.dataTransferObjects

import com.mrdigitalpartners.oprah.core.Questionnaire

data class GetQuestionnairesResponse(val questionnaires: Iterable<Questionnaire>)

data class GetQuestionnaireResponse(val questionnaire: Questionnaire)
