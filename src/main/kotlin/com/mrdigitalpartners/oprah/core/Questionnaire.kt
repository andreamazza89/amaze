package com.mrdigitalpartners.oprah.core

import java.time.ZonedDateTime
import java.util.UUID

enum class QuestionnaireStatus(status: String) {
    NEW("NEW"),
    NOT_STARTED("NOT_STARTED"),
    IN_PROGRESS("IN_PROGRESS"),
    APPROVED("APPROVED")
}

data class Questionnaire(
    val id: UUID,
    val sections: List<Section>,
    val name: String,
    val status: QuestionnaireStatus,
    val submissionDueInWeeks: Int?,
    val sentToPartnerAt: ZonedDateTime?,
    val dueDateStatus: DueDateStatus?
) {
    // query
    fun getSectionByTitle(title: String): Section = this.sections.find { it.title == title }!!

    // update
    fun answerQuestions(answersToQuestions: List<QuestionUpdate<String>>): Questionnaire =
        this.withSections(this.sections.answerQuestions(answersToQuestions.toList()))

    fun requestClarificationOnQuestions(vararg clarificationsToQuestions: QuestionUpdate<String>): Questionnaire =
        this.withSections(this.sections.requestForClarificationsOnQuestions(clarificationsToQuestions.toList()))

    fun approveSection(sectionTitle: String): Questionnaire =
        this.withSections(this.sections.approveSection(sectionTitle))

    fun updateSectionAssignee(sectionTitle: String, assignedTo: String): Questionnaire =
        this.withSections(this.sections.updateAssignee(sectionTitle, assignedTo))

    fun updateSectionStatus(sectionTitle: String, status: SectionStatus): Questionnaire =
        this.withSections(this.sections.updateStatus(sectionTitle, status))

    fun approveAllSectionQuestionAnswers(sectionTitle: String): Questionnaire =
        this.withSections(this.sections.approveAllSectionQuestions(sectionTitle))

    // withers
    private fun withSections(sections: List<Section>): Questionnaire = this.copy(sections = sections)

    fun withStatus(status: QuestionnaireStatus): Questionnaire = this.copy(status = status)

    fun withSubmissionDue(submissionDueInWeeks: Int): Questionnaire =
        this.copy(submissionDueInWeeks = submissionDueInWeeks)
}
