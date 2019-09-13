package com.mrdigitalpartners.oprah.core.questionnaireLifecycle

import com.mrdigitalpartners.oprah.core.QuestionUpdate
import com.mrdigitalpartners.oprah.core.Questionnaire
import com.mrdigitalpartners.oprah.core.Section
import com.mrdigitalpartners.oprah.utils.pipe

class LifecycleService {
    fun process(
        action: Action,
        questionnaire: Questionnaire
    ): Questionnaire {
        return updateSectionStatuses(action, questionnaire)
            .pipe { updateQuestionnaireStatus(action, it) }
            .pipe { furtherDataTransformations(action, it) }
    }

    private fun furtherDataTransformations(action: Action, questionnaire: Questionnaire) =
        when (action) {
            is Action.AllSections.SubmitToPartner -> submitToPartner(questionnaire, action.submissionDueInWeeks)
            is Action.AllSections.QuestionnaireAcknowledgedByPartner -> questionnaire
            is Action.SomeSections.SaveSections -> questionnaire.answerQuestions(action.newAnswers)
            is Action.SingleSection.SubmitSection -> submitSection(questionnaire, action.sectionToSubmit)
            is Action.SingleSection.AssignUserToSection -> assignUserToSection(
                questionnaire,
                action.sectionTitle,
                action.username
            )
            is Action.SingleSection.ReturnSectionToPartner -> returnSectionToPartner(
                questionnaire,
                action.sectionToReturn
            )
            is Action.SingleSection.ApproveSection -> questionnaire.approveSection(action.sectionTitle)
            is Action.SomeSections.AcknowledgeReturnedSections -> questionnaire
            is Action.SomeSections.AcknowledgeReSubmittedSections -> questionnaire
        }

    private fun submitToPartner(questionnaire: Questionnaire, submissionDueInWeeks: Int): Questionnaire =
        questionnaire.withSubmissionDue(submissionDueInWeeks)

    private fun assignUserToSection(
        questionnaire: Questionnaire,
        sectionTitle: String,
        username: String
    ): Questionnaire {
        throwIfSectionCannotBeAssigned(questionnaire.getSectionByTitle(sectionTitle))
        return questionnaire
            .updateSectionAssignee(sectionTitle, username)
            .approveAllSectionQuestionAnswers(sectionTitle)
    }

    private fun returnSectionToPartner(questionnaire: Questionnaire, sectionToReturn: Section): Questionnaire {
        val updatedSections =
            questionnaire.sections.map { existingSection -> updateSectionQuestions(existingSection, sectionToReturn) }
        return questionnaire.copy(sections = updatedSections)
    }

    private fun throwIfSectionCannotBeAssigned(section: Section) {
        if (section.assignedTo != null) {
            throw QuestionnaireLifecycleException("Cannot assign user: section is already assigned.")
        }
    }

    private fun submitSection(questionnaire: Questionnaire, sectionWithUpdates: Section): Questionnaire {
        throwIfSubmittedSectionIsInvalid(sectionWithUpdates)
        val updates = sectionWithUpdates.questions.map { QuestionUpdate(sectionWithUpdates.title, it.id, it.answer!!) }
        return questionnaire.answerQuestions(updates)
    }

    private fun updateSectionQuestions(existingSection: Section, sectionWithUpdates: Section): Section {
        return if (existingSection.title == sectionWithUpdates.title) {
            existingSection.copy(questions = sectionWithUpdates.questions)
        } else {
            existingSection
        }
    }

    private fun throwIfSubmittedSectionIsInvalid(section: Section) {
        if (section.hasUnansweredQuestions()) {
            throw QuestionnaireLifecycleException("Submitted section must have all questions answered")
        }
    }
}

class QuestionnaireLifecycleException(message: String) : Exception(message)