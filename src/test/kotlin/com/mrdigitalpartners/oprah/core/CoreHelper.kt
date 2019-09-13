package com.mrdigitalpartners.oprah.core

import com.mrdigitalpartners.oprah.utils.curried
import java.util.UUID

class CoreHelper {
    // query
    fun firstSection(questionnaire: Questionnaire) = questionnaire.sections.first()

    // create
    fun section(
        vararg questions: Question = defaultedQuestions,
        status: SectionStatus = SectionStatus.NOT_STARTED,
        title: String = "Defaulted section title",
        assignedTo: String? = null
    ) = Section(title = title, questions = questions.toList(), status = status, assignedTo = assignedTo)

    fun notStartedSection(vararg questions: Question = defaultedQuestions, title: String = "Defaulted section title") =
        section(title = title, questions = *questions, status = SectionStatus.NOT_STARTED)

    fun inProgressSection(
        vararg questions: Question = defaultedQuestions,
        title: String = "Defaulted section title",
        assignedTo: String? = null
    ) = section(title = title, questions = *questions, status = SectionStatus.IN_PROGRESS, assignedTo = assignedTo)

    fun submittedSection(
        vararg questions: Question = defaultedQuestions,
        title: String = "Defaulted section title",
        assignedTo: String? = null
    ) = section(title = title, questions = *questions, status = SectionStatus.SUBMITTED, assignedTo = assignedTo)

    fun underReviewSection(
        vararg questions: Question = defaultedQuestions,
        title: String = "Defaulted section title",
        assignedTo: String? = null
    ) = section(title = title, questions = *questions, status = SectionStatus.UNDER_REVIEW, assignedTo = assignedTo)

    fun approvedSection(vararg questions: Question = defaultedQuestions, title: String = "Defaulted section title") =
        section(title = title, questions = *questions, status = SectionStatus.APPROVED)

    fun returnedSection(vararg questions: Question = defaultedQuestions, title: String = "Defaulted section title") =
        section(title = title, questions = *questions, status = SectionStatus.RETURNED)

    fun newQuestionnaire(
        id: UUID = UUID.randomUUID(),
        vararg sections: Section = defaultedSections,
        name: String = "Defaulted questionnaire name"
    ) = questionnaire(id = id, sections = *sections, name = name, status = QuestionnaireStatus.NEW)

    fun inProgressQuestionnaire(
        id: UUID = UUID.randomUUID(),
        vararg sections: Section = defaultedSections,
        name: String = "Defaulted questionnaire name"
    ) = questionnaire(id = id, sections = *sections, name = name, status = QuestionnaireStatus.IN_PROGRESS)

    private fun questionnaire(
        id: UUID,
        vararg sections: Section,
        name: String,
        status: QuestionnaireStatus
    ) = Questionnaire(
        id = id,
        sections = sections.toList(),
        name = name,
        status = status,
        dueDateStatus = null,
        submissionDueInWeeks = null,
        sentToPartnerAt = null
    )

    fun templateQuestion(label: String = "Defaulted TemplateQuestion", requiresSupportingDocuments: Boolean = true) =
        TemplateQuestion(label = label, requiresSupportingDocuments = requiresSupportingDocuments)

    private val defaultedQuestions = arrayOf(question())

    fun question(
        answer: String? = null,
        reviewDetails: ReviewDetails? = reviewDetails(),
        id: UUID = UUID.randomUUID(),
        supportingDocuments: List<SupportingDocument> = emptyList()
    ): Question =
        Question(
            templateQuestion = templateQuestion(),
            answer = answer,
            id = id,
            supportingDocuments = supportingDocuments,
            reviewDetails = reviewDetails
        )

    fun reviewDetails(
        status: ReviewStatus = ReviewStatus.CLARIFICATION_REQUESTED,
        clarificationMessages: List<String> = emptyList()
    ) =
        ReviewDetails(
            status = status,
            requestsForClarification = clarificationMessages
        )

    fun supportingDocument(): SupportingDocument = SupportingDocument(
        id = UUID.randomUUID(),
        fileReference = UUID.randomUUID(),
        providedAsRequired = false,
        fileName = "Defaulted file name",
        fileType = "Defaulted file type"
    )

    val defaultedSections = arrayOf(notStartedSection(*defaultedQuestions))

    fun user() = User("Default user")

    // update
    val updateSections = ::updateSections_.curried()

    private fun updateSections_(toSection: (Section) -> Section, questionnaire: Questionnaire) =
        questionnaire.copy(sections = questionnaire.sections.map { toSection(it) })

    fun requestClarificationOnAllQuestions(section: Section, clarificationRequestMessage: String): Section =
        section.withQuestions(section.questions.map {
            it.withReviewDetails(reviewDetails(clarificationMessages = listOf(clarificationRequestMessage)))
        })

    fun addSupportingDocumentToAllQuestions(
        questionnaire: Questionnaire,
        supportingDocumentToAdd: SupportingDocument
    ): Questionnaire =
        questionnaire.copy(
            sections = questionnaire.sections.map { section ->
                section.copy(questions = section.questions.map { question ->
                    question.copy(supportingDocuments = question.supportingDocuments + supportingDocumentToAdd)
                })
            }
        )

    fun answerAllQuestionsWith(answer: String, section: Section) =
        section.copy(questions = section.questions.map { it.copy(answer = answer) })
}
