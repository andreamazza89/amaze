package com.mrdigitalpartners.oprah.persistence

import com.mrdigitalpartners.oprah.core.CoreHelper
import com.mrdigitalpartners.oprah.core.Question
import com.mrdigitalpartners.oprah.core.Questionnaire
import com.mrdigitalpartners.oprah.core.QuestionnaireStatus
import com.mrdigitalpartners.oprah.core.Section
import com.mrdigitalpartners.oprah.core.TemplateQuestion
import com.mrdigitalpartners.oprah.core.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DatabaseHelper(
    @Autowired val questionnairesRepository: QuestionnairesRepository,
    @Autowired val questionsRepository: QuestionsRepository
) {
    private val coreHelper = CoreHelper()

    fun teardown() {
        questionnairesRepository.deleteAll()
    }

    fun saveQuestionnaire(
        id: UUID,
        partnerId: String,
        questionnaireStatus: QuestionnaireStatus = QuestionnaireStatus.NEW,
        sections: List<Section> = listOf(coreHelper.section(questions = *arrayOf(createQuestion()))),
        name: String = "Default questionnaire name",
        createdBy: User = coreHelper.user()
    ) {
        questionnairesRepository.save(
                partnerId,
                Questionnaire(
                    id = id,
                    sections = sections,
                    name = name,
                    status = questionnaireStatus,
                    dueDateStatus = null,
                    submissionDueInWeeks = null,
                    sentToPartnerAt = null
                ),
                createdBy = createdBy
        )
    }

    fun createQuestion(label: String = "Default floury question to ask bakers"): Question {
        val templateQuestion = saveTemplateQuestion(label = label)
        return Question(
            templateQuestion = templateQuestion,
            answer = null,
            id = UUID.randomUUID(),
            supportingDocuments = emptyList(),
            reviewDetails = null
        )
    }

    fun saveTemplateQuestion(
        id: UUID = UUID.randomUUID(),
        label: String = "Default question",
        createdBy: User = coreHelper.user()
    ) =
        questionsRepository.save(
            TemplateQuestion(
                id = id,
                label = label,
                requiresSupportingDocuments = false
            ),
            createdBy
        )
}
