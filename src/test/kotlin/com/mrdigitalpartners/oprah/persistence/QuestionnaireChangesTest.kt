package com.mrdigitalpartners.oprah.persistence

import com.mrdigitalpartners.oprah.core.CoreHelper
import com.mrdigitalpartners.oprah.core.QuestionUpdate
import com.mrdigitalpartners.oprah.core.QuestionnaireStatus
import com.mrdigitalpartners.oprah.core.ReviewDetails
import com.mrdigitalpartners.oprah.core.ReviewStatus
import com.mrdigitalpartners.oprah.core.SectionStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import java.util.UUID

class QuestionnaireChangesTest {
    private val coreHelper = CoreHelper()

    @Test
    fun `empty changes when the questionnaire is the same`() {
        val questionnaire = coreHelper.newQuestionnaire()

        val changes = changes(questionnaire, questionnaire)

        assertEquals(QuestionnaireChanges(id = questionnaire.id, status = null, sections = null), changes)
    }

    @Test
    fun `questionnaire status changes`() {
        val questionnaire = coreHelper.newQuestionnaire()

        val changes = changes(questionnaire, questionnaire.withStatus(QuestionnaireStatus.NOT_STARTED))

        assertEquals(QuestionnaireChanges(id = questionnaire.id, status = QuestionnaireStatus.NOT_STARTED), changes)
    }

    @Test
    fun `questionnaire submissionDueInWeeks changes`() {
        val submissionDueInWeeks = 4
        val questionnaire = coreHelper.newQuestionnaire()

        val changes = changes(
            questionnaire,
            questionnaire.withSubmissionDue(submissionDueInWeeks)
        )

        assertEquals(QuestionnaireChanges(id = questionnaire.id, submissionDueInWeeks = submissionDueInWeeks), changes)
    }

    @Test
    fun `section status changes`() {
        val sectionOne = coreHelper.notStartedSection(title = "Section one")
        val sectionTwo = coreHelper.notStartedSection(title = "Section two")
        val questionnaireBefore = coreHelper.newQuestionnaire(sections = *arrayOf(sectionOne, sectionTwo))
        val questionnaireAfter = questionnaireBefore.updateSectionStatus("Section two", SectionStatus.APPROVED)

        val changes = changes(questionnaireBefore, questionnaireAfter)

        assertEquals(
            QuestionnaireChanges(
                id = questionnaireBefore.id,
                sections = listOf(
                    SectionChanges(
                        identifier = "Section two",
                        status = SectionStatus.APPROVED
                    )
                )
            ), changes
        )
    }

    @Test
    fun `section assignee changes`() {
        val sectionOne = coreHelper.notStartedSection(title = "Section one")
        val sectionTwo = coreHelper.notStartedSection(title = "Section two")
        val questionnaireBefore = coreHelper.newQuestionnaire(sections = *arrayOf(sectionOne, sectionTwo))
        val questionnaireAfter = questionnaireBefore.updateSectionAssignee("Section one", "The baker")

        val changes = changes(questionnaireBefore, questionnaireAfter)

        assertEquals(
            QuestionnaireChanges(
                id = questionnaireBefore.id,
                sections = listOf(
                    SectionChanges(
                        identifier = "Section one",
                        assignedTo = "The baker"
                    )
                )
            ), changes
        )
    }

    @Test
    fun `question answer changes`() {
        val questionId = UUID.randomUUID()
        val sectionTitle = "Section one"
        val section = coreHelper.notStartedSection(
            title = sectionTitle,
            questions = *arrayOf(coreHelper.question(id = questionId, answer = null))
        )
        val questionnaireBefore = coreHelper.newQuestionnaire(sections = *arrayOf(section))
        val answer = "yes, I do love the bread"
        val update = QuestionUpdate(sectionTitle, questionId, answer)
        val questionnaireAfter = questionnaireBefore.answerQuestions(listOf(update))

        val changes = changes(questionnaireBefore, questionnaireAfter)

        assertEquals(
            QuestionnaireChanges(
                id = questionnaireBefore.id,
                sections = listOf(
                    SectionChanges(
                        identifier = sectionTitle,
                        questions = listOf(QuestionChanges(questionId, answer))
                    )
                )
            ), changes
        )
    }

    @Test
    fun `question supporting documents change`() {
        val questionId = UUID.randomUUID()
        val section = coreHelper.notStartedSection(
            title = "Section one",
            questions = *arrayOf(
                coreHelper.question(
                    id = questionId,
                    supportingDocuments = listOf(coreHelper.supportingDocument(), coreHelper.supportingDocument())
                )
            )
        )
        val questionnaireBefore = coreHelper.newQuestionnaire(sections = *arrayOf(section))
        val newSupportingDocument = coreHelper.supportingDocument()
        val questionnaireAfter =
            coreHelper.addSupportingDocumentToAllQuestions(questionnaireBefore, newSupportingDocument)

        val changes = changes(questionnaireBefore, questionnaireAfter)

        assertEquals(
            QuestionnaireChanges(
                id = questionnaireBefore.id,
                sections = listOf(
                    SectionChanges(
                        identifier = "Section one",
                        questions = listOf(
                            QuestionChanges(
                                questionId,
                                supportingDocument = newSupportingDocument
                            )
                        )
                    )
                )
            ), changes
        )
    }

    @Test
    fun `question requestsForClarification change (from null ReviewDetail to approved with no clarification messages)`() {
        val questionId = UUID.randomUUID()
        val sectionTitle = "Section one"
        val section = coreHelper.notStartedSection(
            title = sectionTitle,
            questions = *arrayOf(
                coreHelper.question(
                    id = questionId,
                    reviewDetails = null
                )
            )
        )

        val questionnaireBefore = coreHelper.newQuestionnaire(sections = *arrayOf(section))
        val questionnaireAfter = questionnaireBefore.approveSection(sectionTitle)

        val changes = changes(questionnaireBefore, questionnaireAfter)

        assertEquals(
            QuestionnaireChanges(
                id = questionnaireBefore.id,
                sections = listOf(
                    SectionChanges(
                        identifier = sectionTitle,
                        questions = listOf(
                            QuestionChanges(
                                questionId,
                                reviewStatus = ReviewStatus.APPROVED
                            )
                        )
                    )
                )
            ), changes
        )
    }

    @Test
    fun `question requestsForClarification change (list of one message represents a change)`() {
        val questionId = UUID.randomUUID()
        val repeatedMessage = "Which Crispy bread do you mean?"
        val section = coreHelper.underReviewSection(
            title = "Section one",
            questions = *arrayOf(
                coreHelper.question(
                    id = questionId,
                    reviewDetails = coreHelper.reviewDetails(clarificationMessages = listOf(repeatedMessage))
                )
            )
        )
        val questionnaireBefore = coreHelper.inProgressQuestionnaire(sections = *arrayOf(section))
        val update = QuestionUpdate("Section one", questionId, repeatedMessage)
        val questionnaireAfter = questionnaireBefore.requestClarificationOnQuestions(update)

        val changes = changes(questionnaireBefore, questionnaireAfter)

        assertEquals(
            QuestionnaireChanges(
                id = questionnaireBefore.id,
                sections = listOf(
                    SectionChanges(
                        identifier = "Section one",
                        questions = listOf(
                            QuestionChanges(
                                questionId,
                                clarificationMessage = repeatedMessage
                            )
                        )
                    )
                )
            ), changes
        )
    }

    @Test
    fun `question requestsForClarification change (empty list represents no change)`() {
        val questionId = UUID.randomUUID()
        val repeatedMessage = "Which Crispy bread do you mean?"
        val section = coreHelper.underReviewSection(
            title = "Section one",
            questions = *arrayOf(
                coreHelper.question(
                    id = questionId,
                    reviewDetails = coreHelper.reviewDetails(clarificationMessages = listOf(repeatedMessage))
                )
            )
        )
        val questionnaireBefore = coreHelper.inProgressQuestionnaire(sections = *arrayOf(section))
        val questionnaireAfter =
            questionnaireBefore.copy(sections = questionnaireBefore.sections.map {
                it.copy(questions = it.questions.map {
                    it.copy(
                        reviewDetails = ReviewDetails(ReviewStatus.APPROVED, emptyList())
                    )
                })
            })

        val changes = changes(questionnaireBefore, questionnaireAfter)

        assertEquals(
            QuestionnaireChanges(
                id = questionnaireBefore.id,
                sections = listOf(
                    SectionChanges(
                        identifier = "Section one",
                        questions = listOf(
                            QuestionChanges(
                                questionId,
                                reviewStatus = ReviewStatus.APPROVED
                            )
                        )
                    )
                )
            ), changes
        )
    }

    @Test
    fun `question review_status changes (from no review detail to needing clarifications)`() {
        val questionId = UUID.randomUUID()
        val section = coreHelper.notStartedSection(
            title = "Section one",
            questions = *arrayOf(
                coreHelper.question(
                    id = questionId,
                    reviewDetails = null
                )
            )
        )

        val questionnaireBefore = coreHelper.newQuestionnaire(sections = *arrayOf(section))
        val questionnaireAfter = questionnaireBefore.copy(
            sections = questionnaireBefore.sections.map {
                it.withQuestions(it.questions.map {
                    it.withReviewDetails(
                        coreHelper.reviewDetails(
                            ReviewStatus.CLARIFICATION_REQUESTED,
                            listOf("Something")
                        )
                    )
                })
            }
        )

        val changes = changes(questionnaireBefore, questionnaireAfter)

        assertEquals(
            QuestionnaireChanges(
                id = questionnaireBefore.id,
                sections = listOf(
                    SectionChanges(
                        identifier = "Section one",
                        questions = listOf(
                            QuestionChanges(
                                questionId,
                                reviewStatus = ReviewStatus.CLARIFICATION_REQUESTED,
                                clarificationMessage = "Something"
                            )
                        )
                    )
                )
            ), changes
        )
    }

    @Test
    fun `question review_status changes`() {
        val questionId = UUID.randomUUID()
        val section = coreHelper.underReviewSection(
            title = "Section one",
            questions = *arrayOf(
                coreHelper.question(
                    id = questionId,
                    reviewDetails = coreHelper.reviewDetails(ReviewStatus.CLARIFICATION_REQUESTED, listOf("message"))
                )
            )
        )
        val questionnaireBefore = coreHelper.inProgressQuestionnaire(sections = *arrayOf(section))
        val questionnaireAfter = questionnaireBefore.copy(
            sections = questionnaireBefore.sections.map {
                it.withQuestions(it.questions.map {
                    it.withReviewDetails(ReviewDetails(ReviewStatus.APPROVED, emptyList()))
                })
            }
        )

        val changes = changes(questionnaireBefore, questionnaireAfter)

        assertEquals(
            QuestionnaireChanges(
                id = questionnaireBefore.id,
                sections = listOf(
                    SectionChanges(
                        identifier = "Section one",
                        questions = listOf(
                            QuestionChanges(
                                questionId,
                                reviewStatus = ReviewStatus.APPROVED
                            )
                        )
                    )
                )
            ), changes
        )
    }

    // Conditionally invoking side effects

    @Test
    fun `does not invoke the given function if the section property has not changed in any of the sections`() {
        val changes = QuestionnaireChanges(
            id = UUID.randomUUID(),
            sections = listOf(
                SectionChanges(
                    identifier = "Section one",
                    status = SectionStatus.APPROVED
                ),
                SectionChanges(
                    identifier = "Section two",
                    status = SectionStatus.APPROVED
                )
            )
        )

        changes.invokeIfSectionPropertyChanged(SectionChanges::assignedTo) {
            fail("this should not be invoked as no assignees have changed")
        }
    }

    @Test
    fun `invokes the given function with a map of sectionTitle_to_newProperty for the given property`() {
        val changes = QuestionnaireChanges(
            id = UUID.randomUUID(),
            sections = listOf(
                SectionChanges(
                    identifier = "Section one",
                    status = SectionStatus.APPROVED
                ),
                SectionChanges(
                    identifier = "Section two",
                    assignedTo = "Crusty"
                ),
                SectionChanges(
                    identifier = "Section three",
                    status = SectionStatus.IN_PROGRESS
                )
            )
        )

        var result: Map<String, SectionStatus> = emptyMap()
        changes.invokeIfSectionPropertyChanged(SectionChanges::status) { sectionTitlesToAnswers ->
            result = sectionTitlesToAnswers
        }
        assertEquals(
            mapOf(
                "Section one" to SectionStatus.APPROVED,
                "Section three" to SectionStatus.IN_PROGRESS
            ), result
        )
    }

    @Test
    fun `does not invoke the given function if the question property has not changed in any of the questions`() {
        val changes = QuestionnaireChanges(
            id = UUID.randomUUID(),
            sections = listOf(
                SectionChanges(
                    identifier = "Section one",
                    status = SectionStatus.APPROVED
                ),
                SectionChanges(
                    identifier = "Section two",
                    status = SectionStatus.APPROVED
                )
            )
        )

        changes.invokeIfQuestionPropertyChanged(QuestionChanges::answer) {
            fail("this should not be invoked as no answers have changed")
        }
    }

    @Test
    fun `invokes the given function with a map of questionId_to_newProperty for the given property`() {
        val uuidOne = UUID.randomUUID()
        val uuidTwo = UUID.randomUUID()
        val uuidThree = UUID.randomUUID()
        val changes = QuestionnaireChanges(
            id = UUID.randomUUID(),
            status = null,
            sections = listOf(
                SectionChanges(
                    identifier = "Section one",
                    questions = listOf(QuestionChanges(uuidOne, "nu answer"))
                ),
                SectionChanges(
                    identifier = "Section two",
                    questions = listOf(QuestionChanges(uuidTwo, "new answer"))
                ),
                SectionChanges(
                    identifier = "Section three",
                    status = SectionStatus.NOT_STARTED
                ),
                SectionChanges(
                    identifier = "Section four",
                    questions = listOf(QuestionChanges(uuidThree, "changed"))
                )
            )
        )

        var result: Map<UUID, String> = emptyMap()
        changes.invokeIfQuestionPropertyChanged(QuestionChanges::answer) { questionIdsToAnswers ->
            result = questionIdsToAnswers
        }
        assertEquals(
            mapOf(
                uuidOne to "nu answer",
                uuidTwo to "new answer",
                uuidThree to "changed"
            ), result
        )
    }
}