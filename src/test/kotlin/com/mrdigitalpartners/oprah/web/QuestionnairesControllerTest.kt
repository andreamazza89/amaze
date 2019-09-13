package com.mrdigitalpartners.oprah.web

import com.mrdigitalpartners.oprah.core.CoreHelper
import com.mrdigitalpartners.oprah.core.DueDateStatus
import com.mrdigitalpartners.oprah.core.DueStatus
import com.mrdigitalpartners.oprah.core.QuestionnaireStatus
import com.mrdigitalpartners.oprah.core.ReviewStatus
import com.mrdigitalpartners.oprah.core.SectionStatus
import com.mrdigitalpartners.oprah.persistence.DatabaseHelper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuestionnairesControllerTest(
    @Autowired val webHelper: WebHelper,
    @Autowired val databaseHelper: DatabaseHelper
) {

    val coreHelper = CoreHelper()

    @AfterEach
    fun teardown() {
        databaseHelper.teardown()
    }

    @Test
    fun `creates a new questionnaire for a partner`() {
        val partnerId = "BBM"
        val questionnaireName = "My first questionnaire"
        val questionsToCreate = listOf(coreHelper.templateQuestion())
        val sectionsToCreate = listOf(webHelper.createSectionRequest(questions = questionsToCreate))

        val questionnaire = webHelper.createQuestionnaire(partnerId, sectionsToCreate, questionnaireName)

        assertEquals(questionnaireName, questionnaire.name)
        assertEquals(QuestionnaireStatus.NEW, questionnaire.status)
        assertEquals(
            questionsToCreate.first().label,
            questionnaire.sections.first().questions.first().templateQuestion.label
        )
    }

    @Test
    fun `submitting to partner updates status to NOT_STARTED`() {
        val id = webHelper.createQuestionnaire().id

        webHelper.submitQuestionnaireToPartner(id)

        val questionnaire = webHelper.getQuestionnaire(id)
        assertEquals(QuestionnaireStatus.NOT_STARTED, questionnaire.status)
    }

    @Test
    fun `questionnaire does not have due dates information when it is created`() {
        val id = webHelper.createQuestionnaire().id

        val questionnaire = webHelper.getQuestionnaire(id)

        assertNull(questionnaire.dueDateStatus)
        assertNull(questionnaire.submissionDueInWeeks)
        assertNull(questionnaire.sentToPartnerAt)
    }

    @Test
    fun `submitting to a partner sets the submission date as well as when it was sent to the partner`() {
        val id = webHelper.createQuestionnaire().id
        val submissionDueInWeeks = 4

        webHelper.submitQuestionnaireToPartner(id, submissionDueInWeeks)

        val questionnaire = webHelper.getQuestionnaire(id)
        val todayInUtc = Instant.now().atZone(ZoneId.of("UTC")).truncatedTo(ChronoUnit.DAYS)
        assertEquals(
            DueDateStatus(todayInUtc.plusWeeks(submissionDueInWeeks.toLong()), DueStatus.SUBMISSION_DUE),
            questionnaire.dueDateStatus
        )
        assertEquals(
            todayInUtc,
            questionnaire.sentToPartnerAt!!.truncatedTo(ChronoUnit.DAYS)
        )
    }

    @Test
    fun `partner acknowledging a NOT_STARTED questionnaire updates all statuses to IN_PROGRESS`() {
        val questionnaire = webHelper.createQuestionnaire()
        webHelper.submitQuestionnaireToPartner(questionnaire.id)

        webHelper.acknowledgeQuestionnaire(questionnaire.id)

        val acknowledgedQuestionnaire = webHelper.getQuestionnaire(questionnaire.id)
        assertEquals(QuestionnaireStatus.IN_PROGRESS, acknowledgedQuestionnaire.status)
        assertTrue(acknowledgedQuestionnaire.sections.all { it.status == SectionStatus.IN_PROGRESS })
    }

    @Test
    fun `finds questionnaires for a partner if there are any`() {
        val partnerOneId = "BBM"
        val partnerTwoId = "OTHER"

        webHelper.createQuestionnaire(partnerOneId)

        val partnerOneQuestionnaires = webHelper.getQuestionnaires(partnerOneId)
        val partnerTwoQuestionnaires = webHelper.getQuestionnaires(partnerTwoId)
        assertEquals(1, partnerOneQuestionnaires.count())
        assertEquals(0, partnerTwoQuestionnaires.count())
    }

    @Test
    fun `partner can save sections`() {
        val answerToSave = "I like saving bread for later!"
        val questionnaire = webHelper.createQuestionnaire(sections = listOf(webHelper.createSectionRequest()))
        webHelper.submitQuestionnaireToPartner(questionnaire.id)
        webHelper.acknowledgeQuestionnaire(questionnaire.id)

        val saveSectionsRequest = webHelper.saveSectionsRequestWithAllQuestionsAnswered(answerToSave, questionnaire)
        webHelper.saveSections(questionnaire.id, saveSectionsRequest)

        val updatedQuestionnaire = webHelper.getQuestionnaire(questionnaire.id)

        val allAnswers = updatedQuestionnaire.sections.flatMap { it.questions }.map { it.answer }
        assertTrue(allAnswers.all { it == answerToSave })
    }

    @Test
    fun `partner can submit a section`() {
        val partnerId = "BBM"
        val answer = "I LOVE BREAD!"
        val questionnaireName = "Bread research"
        val sectionTitle = "bread section"
        val sectionsRequest = listOf(webHelper.createSectionRequest(sectionTitle))
        val questionnaire =
            webHelper.createQuestionnaire(partnerId, name = questionnaireName, sections = sectionsRequest)
        webHelper.submitQuestionnaireToPartner(questionnaire.id)
        webHelper.acknowledgeQuestionnaire(questionnaire.id)

        webHelper.answerAllSectionQuestionsWith(questionnaire.id, sectionTitle, answer)

        val updatedQuestionnaire = webHelper.getQuestionnaire(questionnaire.id)
        val firstSection = coreHelper.firstSection(updatedQuestionnaire)
        assertEquals(SectionStatus.SUBMITTED, firstSection.status)
        assertTrue(firstSection.questions.all { it.answer == answer })
    }

    @Test
    fun `mrdp user can assign themselves to a section once it has been submitted (defaults all questions to APPROVED)`() {
        val partnerId = "BBM"
        val username = "Crusty roll"
        val questionnaireName = "Bread research"
        val sectionTitle = "Section title"
        val sectionsRequest = listOf(webHelper.createSectionRequest(sectionTitle))
        val questionnaire =
            webHelper.createQuestionnaire(partnerId, name = questionnaireName, sections = sectionsRequest)
        webHelper.submitQuestionnaireToPartner(questionnaire.id)
        webHelper.acknowledgeQuestionnaire(questionnaire.id)
        webHelper.answerAllSectionQuestionsWith(questionnaire.id, sectionTitle, "test answer")

        webHelper.assignMrdpUserToSection(questionnaire.id, sectionTitle, username)
        val updatedSection = coreHelper.firstSection(webHelper.getQuestionnaire(questionnaire.id))

        assertEquals(SectionStatus.UNDER_REVIEW, updatedSection.status)
        assertEquals(username, updatedSection.assignedTo)
        assertTrue(updatedSection.questions.all { it.isApproved() })
    }

    @Test
    fun `mrdp user can approve section`() {
        val partnerId = "BBM"
        val username = "Crusty roll"
        val questionnaireName = "Bread research"
        val sectionTitle = "Section title"
        val sectionsRequest = listOf(webHelper.createSectionRequest(sectionTitle))
        val questionnaire =
            webHelper.createQuestionnaire(partnerId, name = questionnaireName, sections = sectionsRequest)
        webHelper.submitQuestionnaireToPartner(questionnaire.id)
        webHelper.acknowledgeQuestionnaire(questionnaire.id)
        webHelper.answerAllSectionQuestionsWith(questionnaire.id, sectionTitle, "I LOVE BREAD")
        webHelper.assignMrdpUserToSection(questionnaire.id, sectionTitle, username)

        webHelper.approveSection(questionnaire.id, username, sectionTitle)
        val updatedSection = coreHelper.firstSection(webHelper.getQuestionnaire(questionnaire.id))

        assertEquals(SectionStatus.APPROVED, updatedSection.status)
        assertTrue(updatedSection.questions.all { it.isApproved() })
    }

    @Test
    fun `mrdp user can return a section to partner`() {
        val partnerId = "BBM"
        val username = "Gayle King (and the Mill)"
        val sectionTitle = "Section title"
        val clarificationRequestMessage = "Please be more specific about the type of bread"
        val sectionsRequest = listOf(webHelper.createSectionRequest(sectionTitle))
        val questionnaire = webHelper.createQuestionnaire(partnerId, sections = sectionsRequest)
        webHelper.submitQuestionnaireToPartner(questionnaire.id)
        webHelper.acknowledgeQuestionnaire(questionnaire.id)
        webHelper.answerAllSectionQuestionsWith(questionnaire.id, sectionTitle, "I LOVE BREAD")
        webHelper.assignMrdpUserToSection(questionnaire.id, sectionTitle, username)

        val returnSectionToPartnerRequest =
            webHelper.createReturnFirstSectionToPartnerRequest(questionnaire.id, clarificationRequestMessage)
        webHelper.returnSectionToPartner(questionnaire.id, returnSectionToPartnerRequest)

        val updatedSection = webHelper.getQuestionnaire(questionnaire.id).sections[0]

        assertEquals(SectionStatus.RETURNED, updatedSection.status)
        assertEquals(
            clarificationRequestMessage,
            updatedSection.questions[0].reviewDetails!!.requestsForClarification[0]
        )
        assertEquals(ReviewStatus.CLARIFICATION_REQUESTED, updatedSection.questions[0].reviewDetails!!.status)
    }

    @Test
    fun `partner acknowledging a questionnaire with RETURNED sections updates them to IN_PROGRESS`() {
        val sectionTitle = "Section title"
        val sectionsRequest = listOf(
            webHelper.createSectionRequest(sectionTitle),
            webHelper.createSectionRequest()
        )
        val questionnaire = webHelper.createQuestionnaire(sections = sectionsRequest)

        webHelper.submitQuestionnaireToPartner(questionnaire.id)
        webHelper.acknowledgeQuestionnaire(questionnaire.id)
        webHelper.answerAllSectionQuestions(questionnaire.id, sectionTitle)
        webHelper.assignMrdpUserToSection(questionnaire.id, sectionTitle)
        webHelper.returnSectionToPartner(questionnaire.id)

        webHelper.acknowledgeReturnedSections(questionnaire.id)

        val reAcknowledgedQuestionnaire = webHelper.getQuestionnaire(questionnaire.id)
        assertTrue(reAcknowledgedQuestionnaire.sections.all { it.status == SectionStatus.IN_PROGRESS })
    }

    @Test
    fun `mrdp user can acknowledge resubmitted sections`() {
        val sectionTitle = "Section title"
        val currentUser = "Gayle King"
        val sectionsRequest = listOf(
            webHelper.createSectionRequest(sectionTitle),
            webHelper.createSectionRequest()
        )
        val questionnaire = webHelper.createQuestionnaire(sections = sectionsRequest)

        webHelper.submitQuestionnaireToPartner(questionnaire.id)
        webHelper.acknowledgeQuestionnaire(questionnaire.id)
        webHelper.answerAllSectionQuestions(questionnaire.id, sectionTitle)
        webHelper.assignMrdpUserToSection(questionnaire.id, sectionTitle, currentUser)
        webHelper.returnSectionToPartner(questionnaire.id)
        webHelper.acknowledgeReturnedSections(questionnaire.id)
        webHelper.resubmitSection(questionnaire.id, sectionTitle)

        webHelper.acknowledgeResubmittedSections(questionnaire.id, currentUser)

        val reAcknowledgedSection =
            webHelper.getQuestionnaire(questionnaire.id).sections.find { it.title == sectionTitle }
        assertEquals(SectionStatus.UNDER_REVIEW, reAcknowledgedSection!!.status)
    }
}
