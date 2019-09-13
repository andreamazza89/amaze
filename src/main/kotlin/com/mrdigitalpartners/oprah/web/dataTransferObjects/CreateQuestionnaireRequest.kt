package com.mrdigitalpartners.oprah.web.dataTransferObjects

import com.mrdigitalpartners.oprah.core.Question
import com.mrdigitalpartners.oprah.core.Questionnaire
import com.mrdigitalpartners.oprah.core.QuestionnaireStatus
import com.mrdigitalpartners.oprah.core.Section
import com.mrdigitalpartners.oprah.core.SectionStatus
import com.mrdigitalpartners.oprah.core.TemplateQuestion
import java.util.UUID

data class CreateQuestionnaireRequest(
    val partnerId: String,
    val sections: List<CreateSectionRequest>,
    val name: String
)

fun newQuestionnaireFromRequest(request: CreateQuestionnaireRequest, id: UUID = UUID.randomUUID()) =
    Questionnaire(
        id = id,
        sections = request.sections.map {
            Section(
                title = it.title,
                questions = it.questions.map(::newQuestionFromTemplate),
                status = SectionStatus.NOT_STARTED
            )
        },
        name = request.name,
        status = QuestionnaireStatus.NEW,
        dueDateStatus = null,
        submissionDueInWeeks = null,
        sentToPartnerAt = null
    )

fun newQuestionFromTemplate(templateQuestion: TemplateQuestion): Question =
    Question(
        templateQuestion,
        id = UUID.randomUUID(),
        supportingDocuments = emptyList(),
        reviewDetails = null
    )
