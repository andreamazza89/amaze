package com.mrdigitalpartners.oprah.core

import com.mrdigitalpartners.oprah.core.questionnaireLifecycle.Action
import com.mrdigitalpartners.oprah.core.questionnaireLifecycle.LifecycleService
import com.mrdigitalpartners.oprah.core.questionnaireLifecycle.QuestionnaireLifecycleException
import com.mrdigitalpartners.oprah.utils.pipe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class LifecycleServiceTest {
    private val coreHelper = CoreHelper()
    private val questionnaireLifecycleService = LifecycleService()

    @Test
    fun `submitting to partner updates a questionnaire status to NOT_STARTED and sets the submission due date`() {
        val submissionDueInWeeks = 4
        val updatedQuestionnaire = questionnaireLifecycleService.process(
            Action.AllSections.SubmitToPartner(submissionDueInWeeks),
            questionnaire(QuestionnaireStatus.NEW)
        )

        assertEquals(QuestionnaireStatus.NOT_STARTED, updatedQuestionnaire.status)
        assertEquals(submissionDueInWeeks, updatedQuestionnaire.submissionDueInWeeks)
    }

    @Test
    fun `a partner acknowledging a new questionnaire updates the questionnaire and sections status to IN_PROGRESS`() {
        val sections = listOf(coreHelper.notStartedSection())
        val updatedQuestionnaire = questionnaireLifecycleService.process(
            Action.AllSections.QuestionnaireAcknowledgedByPartner,
            questionnaire(QuestionnaireStatus.NOT_STARTED, sections)
        )

        assertEquals(QuestionnaireStatus.IN_PROGRESS, updatedQuestionnaire.status)
        assertEquals(SectionStatus.IN_PROGRESS, updatedQuestionnaire.sections.first().status)
    }

    @Test
    fun `allows IN_PROGRESS sections to be saved with no answers`() {
        val sections = listOf(coreHelper.inProgressSection())
        val updatedQuestionnaire = questionnaireLifecycleService.process(
            Action.SomeSections.SaveSections(sections.map { it.title }, emptyList()),
            questionnaire(QuestionnaireStatus.IN_PROGRESS, sections)
        )

        assertEquals(QuestionnaireStatus.IN_PROGRESS, updatedQuestionnaire.status)
        assertTrue(updatedQuestionnaire.sections.all { it.status == SectionStatus.IN_PROGRESS })
    }

    @Test
    fun `allows IN_PROGRESS sections to be saved with answers`() {
        val questionOne = coreHelper.question()
        val questionTwo = coreHelper.question()
        val answerOne = "I like to save my breadstix for later!"
        val answerTwo = "The answer is always bread"
        val sectionTitle = "Section to save"
        val inProgressSection = coreHelper.inProgressSection(title = sectionTitle, questions = *arrayOf(questionOne, questionTwo))
        val submittedSection = coreHelper.submittedSection()
        val questionnaire = coreHelper.inProgressQuestionnaire(sections = *arrayOf(inProgressSection, submittedSection))

        val questionUpdates = listOf(
            questionUpdate(sectionTitle, questionOne.id, answerOne),
            questionUpdate(sectionTitle, questionTwo.id, answerTwo)
        )
        val updatedQuestionnaire = questionnaireLifecycleService.process(
            Action.SomeSections.SaveSections(listOf(sectionTitle), questionUpdates),
            questionnaire
        )

        assertEquals(answerOne, updatedQuestionnaire.sections[0].questions[0].answer)
        assertEquals(answerTwo, updatedQuestionnaire.sections[0].questions[1].answer)
        assertEquals(submittedSection, updatedQuestionnaire.sections[1])
    }

    @Test
    fun `updates section to SUBMITTED`() {
        val sectionOne = coreHelper.inProgressSection(title = "first section")
        val sectionTwo = coreHelper.inProgressSection(title = "second section")

        val sectionToSubmit = answerAllQuestions(sectionTwo)
        val updatedQuestionnaire = questionnaireLifecycleService.process(
            Action.SingleSection.SubmitSection(sectionToSubmit.title, sectionToSubmit),
            questionnaire(QuestionnaireStatus.IN_PROGRESS, listOf(sectionOne, sectionTwo))
        )

        assertEquals(SectionStatus.IN_PROGRESS, updatedQuestionnaire.sections[0].status)
        assertEquals(SectionStatus.SUBMITTED, updatedQuestionnaire.sections[1].status)
    }

    @Test
    fun `fails if any submitted sections have missing answers`() {
        val section = coreHelper.inProgressSection()

        assertThrows(QuestionnaireLifecycleException::class.java) {
            questionnaireLifecycleService.process(
                Action.SingleSection.SubmitSection(section.title, section),
                questionnaire(QuestionnaireStatus.IN_PROGRESS, listOf(section))
            )
        }
    }

    @Test
    fun `updates sections with answers`() {
        val section = coreHelper.inProgressSection()
        val updatedQuestionnaire = questionnaireLifecycleService.process(
            Action.SingleSection.SubmitSection(section.title, answerAllQuestions(section, "the answer is bread")),
            questionnaire(QuestionnaireStatus.IN_PROGRESS, listOf(section))
        )

        assertEquals("the answer is bread", coreHelper.firstSection(updatedQuestionnaire).questions.first().answer)
    }

    @Test
    fun `updates a submitted section with an assigned user`() {
        val sectionOneTitle = "first section"
        val sectionTwoTitle = "second section"
        val username = "Crusty Roll"
        val sectionOne = coreHelper.submittedSection(title = sectionOneTitle)
        val sectionTwo = coreHelper.submittedSection(title = sectionTwoTitle)
        val questionnaire = inProgressQuestionnaire(sectionOne, sectionTwo)

        val updatedQuestionnaire = questionnaireLifecycleService.process(
            Action.SingleSection.AssignUserToSection(sectionOneTitle, username),
            questionnaire
        )

        assertEquals(SectionStatus.UNDER_REVIEW, updatedQuestionnaire.sections[0].status)
        assertEquals(SectionStatus.SUBMITTED, updatedQuestionnaire.sections[1].status)
        assertEquals(username, updatedQuestionnaire.sections[0].assignedTo)
    }

    @Test
    fun `defaults all questions to APPROVED when user is assigned`() {
        val sectionOneTitle = "first section"
        val sectionOne = coreHelper.submittedSection(title = sectionOneTitle)
        val questionnaire = inProgressQuestionnaire(sectionOne)

        val updatedQuestionnaire = questionnaireLifecycleService.process(
            Action.SingleSection.AssignUserToSection(sectionOneTitle, "Crusty Roll"),
            questionnaire
        )

        assertTrue(updatedQuestionnaire.sections[0].questions.all { it.isApproved() })
    }

    @Test
    fun `fails to assign a user to a section that is already assigned, regardless of the status`() {
        SectionStatus.values().forEach { sectionStatus ->
            assertThrows(QuestionnaireLifecycleException::class.java) {
                val sectionTitle = "A Section"
                val username = "Crusty Roll"
                val section = coreHelper.section(title = sectionTitle, status = sectionStatus)
                val questionnaire = inProgressQuestionnaire(section)
                    .pipe(coreHelper.updateSections { it.copy(assignedTo = username) })

                questionnaireLifecycleService.process(
                    Action.SingleSection.AssignUserToSection(sectionTitle, username),
                    questionnaire
                )
            }
        }
    }

    @Test
    fun `fails to assign a user to a section unless it has SUBMITTED status`() {
        SectionStatus.values().filter { it != SectionStatus.SUBMITTED }.forEach { sectionStatus ->
            assertThrows(QuestionnaireLifecycleException::class.java) {
                val sectionTitle = "A Section"
                val username = "Crusty Roll"
                val section = coreHelper.section(title = sectionTitle, status = sectionStatus)
                val questionnaire = inProgressQuestionnaire(section)

                questionnaireLifecycleService.process(
                    Action.SingleSection.AssignUserToSection(sectionTitle, username),
                    questionnaire
                )
            }
        }
    }

    @Test
    fun `returns a section to a partner`() {
        val section = coreHelper.underReviewSection()
        val questionnaire = inProgressQuestionnaire(section)
        val requestForClarificationMessage = "What do you mean you don't like bread?!"

        val returnedSection = coreHelper.requestClarificationOnAllQuestions(section, requestForClarificationMessage)
        val updatedQuestionnaire = questionnaireLifecycleService.process(
            Action.SingleSection.ReturnSectionToPartner(returnedSection.title, returnedSection),
            questionnaire
        )

        assertEquals(SectionStatus.RETURNED, updatedQuestionnaire.sections[0].status)

        val allRequestsForClarificationUpdated = updatedQuestionnaire.sections.all {
            it.questions.all { question ->
                question.reviewDetails!!.requestsForClarification == listOf(requestForClarificationMessage)
            }
        }
        assertTrue(allRequestsForClarificationUpdated)
    }

    @Test
    fun `a partner acknowledging returned sections updates the returned sections' status to IN_PROGRESS`() {
        val returned = coreHelper.returnedSection(title = "one")
        val inProgress = coreHelper.inProgressSection(title = "two")
        val approved = coreHelper.approvedSection(title = "three")

        val sections = listOf(returned, inProgress, approved)
        val updatedQuestionnaire = questionnaireLifecycleService.process(
            Action.SomeSections.AcknowledgeReturnedSections(listOf(returned.title)),
            questionnaire(QuestionnaireStatus.IN_PROGRESS, sections)
        )

        assertEquals(SectionStatus.IN_PROGRESS, updatedQuestionnaire.sections[0].status)
        assertEquals(SectionStatus.IN_PROGRESS, updatedQuestionnaire.sections[1].status)
        assertEquals(SectionStatus.APPROVED, updatedQuestionnaire.sections[2].status)
    }

    @Test
    fun `approves a section`() {
        val sectionOneTitle = "first section"
        val questionOne = coreHelper.question(reviewDetails = null)
        val questionTwo = coreHelper.question(reviewDetails = coreHelper.reviewDetails())
        val sectionOne =
            coreHelper.underReviewSection(title = sectionOneTitle, questions = *arrayOf(questionOne, questionTwo))
        val sectionTwoTitle = "second section"
        val sectionTwo = coreHelper.submittedSection(title = sectionTwoTitle)
        val questionnaire = inProgressQuestionnaire(sectionOne, sectionTwo)

        val updatedQuestionnaire = questionnaireLifecycleService.process(
            Action.SingleSection.ApproveSection(sectionOneTitle),
            questionnaire
        )

        assertEquals(SectionStatus.APPROVED, updatedQuestionnaire.sections[0].status)
        assertEquals(sectionTwo, updatedQuestionnaire.sections[1])
        assertTrue(updatedQuestionnaire.sections[0].questions.all { it.reviewDetails!!.status == ReviewStatus.APPROVED })
    }

    @Test
    fun `fails to approve section unless it is UNDER_REVIEW`() {
        SectionStatus.values().filter { it != SectionStatus.UNDER_REVIEW }.forEach { sectionStatus ->
            assertThrows(QuestionnaireLifecycleException::class.java) {
                val sectionTitle = "A Section"
                val section = coreHelper.section(title = sectionTitle, status = sectionStatus)
                val questionnaire = inProgressQuestionnaire(section)

                questionnaireLifecycleService.process(
                    Action.SingleSection.ApproveSection(sectionTitle),
                    questionnaire
                )
            }
        }
    }

    @Test
    fun `transitions sections to UNDER_REVIEW when acknowledgingResubmittedSections`() {
        val section = coreHelper.submittedSection()
        val questionnaire = inProgressQuestionnaire(section)

        val updatedQuestionnaire = questionnaireLifecycleService.process(
            Action.SomeSections.AcknowledgeReSubmittedSections(listOf(section.title)),
            questionnaire
        )

        assertEquals(SectionStatus.UNDER_REVIEW, updatedQuestionnaire.sections[0].status)
    }

    @Test
    fun `marks questionnaire as APPROVED when all sections are APPROVED`() {
        val sectionOneTitle = "first section"
        val sectionTwoTitle = "second section"
        val sectionOne = coreHelper.underReviewSection(title = sectionOneTitle)
        val sectionTwo = coreHelper.approvedSection(title = sectionTwoTitle)
        val questionnaire = inProgressQuestionnaire(sectionOne, sectionTwo)

        val updatedQuestionnaire = questionnaireLifecycleService.process(
            Action.SingleSection.ApproveSection(sectionOneTitle),
            questionnaire
        )

        assertEquals(QuestionnaireStatus.APPROVED, updatedQuestionnaire.status)
    }

    private fun questionUpdate(sectionTitle: String, id: UUID, answer: String) =
        QuestionUpdate(sectionTitle, id, answer)

    private fun answerAllQuestions(section: Section, answer: String = "the answer is always bread"): Section {
        return section.copy(questions = section.questions.map { it.copy(answer = answer) })
    }

    private fun inProgressQuestionnaire(vararg sections: Section = coreHelper.defaultedSections) =
        questionnaire(QuestionnaireStatus.IN_PROGRESS, sections.toList())

    private fun questionnaire(status: QuestionnaireStatus, sections: List<Section> = emptyList()) = Questionnaire(
        id = UUID.randomUUID(),
        sections = sections,
        name = "Questionnaire",
        status = status,
        dueDateStatus = null,
        submissionDueInWeeks = null,
        sentToPartnerAt = null
    )
}