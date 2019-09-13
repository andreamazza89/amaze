package com.mrdigitalpartners.oprah.web

import com.mrdigitalpartners.oprah.core.QuestionUpdate
import com.mrdigitalpartners.oprah.core.Questionnaire
import com.mrdigitalpartners.oprah.core.SectionStatus
import com.mrdigitalpartners.oprah.core.questionnaireLifecycle.Action
import com.mrdigitalpartners.oprah.web.dataTransferObjects.SaveSectionsRequest
import com.mrdigitalpartners.oprah.web.dataTransferObjects.SectionUpdateRequest

class ActionBuilder {
    fun acknowledgeReturnedSections(questionnaire: Questionnaire) =
        Action.SomeSections.AcknowledgeReturnedSections(
            questionnaire.sections.filter { it.status == SectionStatus.RETURNED }.map { it.title })

    fun acknowledgeResubmittedSections(questionnaire: Questionnaire, username: String): Action {
        val sectionTitles = questionnaire.sections
            .filter { it.status == SectionStatus.SUBMITTED && it.assignedTo == username }
            .map { it.title }
        return Action.SomeSections.AcknowledgeReSubmittedSections(sectionTitles)
    }

    fun saveSections(saveSectionsRequest: SaveSectionsRequest) =
        Action.SomeSections.SaveSections(
            saveSectionsRequest.sectionUpdates.map { it.title },
            saveSectionsRequest.sectionUpdates.flatMap(::toQuestionUpdates)
        )

    private fun toQuestionUpdates(sectionUpdateRequest: SectionUpdateRequest): List<QuestionUpdate<String>> =
        sectionUpdateRequest.questionUpdates.map { QuestionUpdate(sectionUpdateRequest.title, it.id, it.newAnswer) }
}