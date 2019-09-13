package com.mrdigitalpartners.oprah.persistence

import com.mrdigitalpartners.oprah.core.Question
import com.mrdigitalpartners.oprah.core.Questionnaire
import com.mrdigitalpartners.oprah.core.QuestionnaireStatus
import com.mrdigitalpartners.oprah.core.ReviewStatus
import com.mrdigitalpartners.oprah.core.Section
import com.mrdigitalpartners.oprah.core.SectionStatus
import com.mrdigitalpartners.oprah.core.SupportingDocument
import com.mrdigitalpartners.oprah.utils.pipe
import java.util.UUID
import kotlin.reflect.KProperty1

data class QuestionnaireChanges(
    val id: UUID,
    val status: QuestionnaireStatus? = null,
    val submissionDueInWeeks: Int? = null,
    val sections: List<SectionChanges>? = null
) {
    fun invokeIfQuestionnaireStatusChanged(invoke: (QuestionnaireStatus) -> Unit) {
        if (this.status != null) {
            invoke(this.status)
        }
    }

    fun invokeIfSubmissionDueInWeeksChanges(invoke: (Int) -> Unit) {
        if (this.submissionDueInWeeks != null) {
            invoke(this.submissionDueInWeeks)
        }
    }

    fun <V> invokeIfSectionPropertyChanged(
        targetProperty: KProperty1<SectionChanges, V?>,
        invoke: (Map<String, V>) -> Unit
    ) = allSectionChanges()
        .filter { changes -> targetProperty.get(changes) != null }
        .map { changes -> Pair(changes.identifier, targetProperty.get(changes)!!) }
        .pipe { it.toMap() }
        .pipe {
            if (it.isNotEmpty()) {
                invoke(it)
            }
        }

    // The bodies of these two functions have pretty much the same shape, however, having
    // tried a couple of ways to generalise these, I (Andrea) think that the best way
    // would be to expose the UUID for a section and then create an abstract class Changes, which
    // both SectionChanges and QuestionChanges implement and has a property `identifier`, with type UUID

    fun <V> invokeIfQuestionPropertyChanged(
        targetProperty: KProperty1<QuestionChanges, V?>,
        invoke: (Map<UUID, V>) -> Unit
    ) = allQuestionChanges()
        .filter { changes -> targetProperty.get(changes) != null }
        .map { changes -> Pair(changes.identifier, targetProperty.get(changes)!!) }
        .pipe { it.toMap() }
        .pipe {
            if (it.isNotEmpty()) {
                invoke(it)
            }
        }

    private fun allSectionChanges() = this.sections.orEmpty()
    private fun allQuestionChanges() = allSectionChanges().flatMap { it.questions.orEmpty() }
}

data class SectionChanges(
    val identifier: String,
    val assignedTo: String? = null,
    val status: SectionStatus? = null,
    val questions: List<QuestionChanges>? = null
)

data class QuestionChanges(
    val identifier: UUID,
    val answer: String? = null,
    val supportingDocument: SupportingDocument? = null,
    val reviewStatus: ReviewStatus? = null,
    val clarificationMessage: String? = null
)

fun changes(before: Questionnaire, after: Questionnaire): QuestionnaireChanges =
    if (before == after) {
        noChanges(before.id)
    } else {
        checkQuestionnaireStatus(before, after)
            .pipe { it.copy(submissionDueInWeeks = checkSubmissionDueInWeeks(before, after).submissionDueInWeeks) }
            .pipe { it.copy(sections = checkSections(before.sections, after.sections)) }
    }

fun checkSubmissionDueInWeeks(before: Questionnaire, after: Questionnaire): QuestionnaireChanges =
    if (before.submissionDueInWeeks == after.submissionDueInWeeks) {
        noChanges(before.id)
    } else {
        QuestionnaireChanges(id = before.id, submissionDueInWeeks = after.submissionDueInWeeks)
    }

fun checkQuestionnaireStatus(before: Questionnaire, after: Questionnaire): QuestionnaireChanges =
    if (before.status == after.status) {
        noChanges(before.id)
    } else {
        QuestionnaireChanges(id = before.id, status = after.status, sections = null)
    }

fun checkSections(sectionsBefore: List<Section>, sectionsAfter: List<Section>): List<SectionChanges>? =
    if (sectionsBefore == sectionsAfter) {
        null
    } else {
        (sectionsBefore zip sectionsAfter)
            .fold(emptyList(), accumulateSectionChanges())
    }

private fun accumulateSectionChanges(): (acc: List<SectionChanges>, Pair<Section, Section>) -> List<SectionChanges> = { sectionsChanges, beforeAfter ->
        if (beforeAfter.first == beforeAfter.second) {
            sectionsChanges
        } else {
            sectionsChanges + generateSectionChanges(beforeAfter)
        }
    }

private fun generateSectionChanges(beforeAfter: Pair<Section, Section>): SectionChanges =
    SectionChanges(
        identifier = beforeAfter.second.title,
        assignedTo = checkProperty(beforeAfter, Section::assignedTo),
        status = checkProperty(beforeAfter, Section::status),
        questions = checkQuestions(beforeAfter.first.questions, beforeAfter.second.questions)
    )

fun checkQuestions(questionsBefore: List<Question>, questionsAfter: List<Question>): List<QuestionChanges>? =
    if (questionsBefore == questionsAfter) {
        null
    } else {
        (questionsBefore zip questionsAfter)
            .fold(emptyList(), accumulateQuestionChanges())
    }

fun accumulateQuestionChanges(): (acc: List<QuestionChanges>, Pair<Question, Question>) -> List<QuestionChanges> = { questionsChanges, beforeAfter ->
        if (beforeAfter.first == beforeAfter.second) {
            questionsChanges
        } else {
            questionsChanges + generateQuestionChanges(beforeAfter)
        }
    }

private fun generateQuestionChanges(beforeAfter: Pair<Question, Question>): QuestionChanges =
    QuestionChanges(
        identifier = beforeAfter.second.id,
        answer = checkProperty(beforeAfter, Question::answer),
        supportingDocument = checkPropertyItems(beforeAfter, Question::supportingDocuments),
        reviewStatus = checkReviewStatus(beforeAfter),
        clarificationMessage = checkClarificationMessageChanges(beforeAfter)
    )

private fun checkReviewStatus(beforeAfter: Pair<Question, Question>): ReviewStatus? =
    if (beforeAfter.first.reviewDetails?.status == beforeAfter.second.reviewDetails?.status) {
        null
    } else {
        beforeAfter.second.reviewDetails?.status
    }

private fun checkClarificationMessageChanges(beforeAfter: Pair<Question, Question>): String? {
    val requestsForClarificationHaveNotChanged =
        (beforeAfter.first.reviewDetails?.requestsForClarification == beforeAfter.second.reviewDetails?.requestsForClarification) || beforeAfter.second.reviewDetails?.requestsForClarification?.isEmpty() ?: true
    return if (requestsForClarificationHaveNotChanged) {
        null
    } else {
        beforeAfter.second.reviewDetails?.requestsForClarification?.last()
    }
}

private fun <R, V> checkProperty(beforeAfter: Pair<R, R>, property: KProperty1<R, V>): V? =
    if (beforeAfter.haveTheSame(property)) {
        null
    } else {
        property.get(beforeAfter.second)
    }

private fun <V> checkPropertyItems(beforeAfter: Pair<Question, Question>, property: KProperty1<Question, List<V>>): V? =
    if (property.get(beforeAfter.first) == property.get(beforeAfter.second)) {
        null
    } else {
        property.get(beforeAfter.second).last()
    }

private fun <R, V> Pair<R, R>.haveTheSame(property: KProperty1<R, V>): Boolean =
    property.get(this.first) == property.get(this.second)

private fun noChanges(id: UUID) = QuestionnaireChanges(id, null, null)
