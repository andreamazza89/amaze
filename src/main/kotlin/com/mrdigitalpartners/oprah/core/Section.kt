package com.mrdigitalpartners.oprah.core

enum class SectionStatus(status: String) {
    NOT_STARTED("NOT_STARTED"),
    IN_PROGRESS("IN_PROGRESS"),
    SUBMITTED("SUBMITTED"),
    UNDER_REVIEW("UNDER_REVIEW"),
    APPROVED("APPROVED"),
    RETURNED("RETURNED")
}

data class Section(
    val assignedTo: String? = null,
    val title: String,
    val questions: List<Question>,
    val status: SectionStatus
) {

    fun answerQuestion(update: QuestionUpdate<String>) =
        this.withQuestions(this.questions.answerQuestion(update))

    fun requestClarification(update: QuestionUpdate<String>) =
        this.withQuestions(this.questions.requestClarification(update))

    fun approveAllAnswers(): Section =
        this.withQuestions(this.questions.map { it.approveAnswer() })

    fun hasUnansweredQuestions(): Boolean =
        this.questions.any { it.answer == null }

    fun withQuestions(questions: List<Question>) =
        this.copy(questions = questions)

    override fun toString(): String {
        return "${this.title} (${this.status}), assigned to: $assignedTo"
    }
}

fun List<Section>.answerQuestions(updates: List<QuestionUpdate<String>>): List<Section> =
    updates.fold(this) { sections, update ->
        sections.updateSection(update.sectionTitle) { it.answerQuestion(update) }
    }

fun List<Section>.approveSection(title: String): List<Section> =
    this.updateSection(title) { it.approveAllAnswers() }

fun List<Section>.requestForClarificationsOnQuestions(updates: List<QuestionUpdate<String>>): List<Section> =
    updates.fold(this) { sections, update ->
        sections.updateSection(update.sectionTitle) { it.requestClarification(update) }
    }

fun List<Section>.updateAssignee(title: String, assignedTo: String): List<Section> =
    this.updateSection(title) { it.copy(assignedTo = assignedTo) }

fun List<Section>.updateStatus(title: String, status: SectionStatus): List<Section> =
    this.updateSection(title) { it.copy(status = status) }

fun List<Section>.approveAllSectionQuestions(title: String): List<Section> =
    this.updateSection(title) { it.withQuestions(it.questions.approveAll()) }

private fun List<Section>.updateSection(title: String, updater: (Section) -> Section): List<Section> =
    this.map { section ->
        if (section.title == title) {
            updater(section)
        } else {
            section
        }
    }
