package com.mrdigitalpartners.oprah.web

import com.mrdigitalpartners.oprah.core.CoreHelper
import com.mrdigitalpartners.oprah.core.Questionnaire
import com.mrdigitalpartners.oprah.core.TemplateQuestion
import com.mrdigitalpartners.oprah.core.TemplateQuestionnaire
import com.mrdigitalpartners.oprah.core.User
import com.mrdigitalpartners.oprah.persistence.DatabaseHelper
import com.mrdigitalpartners.oprah.web.dataTransferObjects.CreateQuestionnaireRequest
import com.mrdigitalpartners.oprah.web.dataTransferObjects.CreateSectionRequest
import com.mrdigitalpartners.oprah.web.dataTransferObjects.GetQuestionnaireResponse
import com.mrdigitalpartners.oprah.web.dataTransferObjects.GetQuestionnairesResponse
import com.mrdigitalpartners.oprah.web.dataTransferObjects.GetTemplatesResponse
import com.mrdigitalpartners.oprah.web.dataTransferObjects.QuestionUpdateRequest
import com.mrdigitalpartners.oprah.web.dataTransferObjects.ReturnSectionToPartnerRequest
import com.mrdigitalpartners.oprah.web.dataTransferObjects.SaveSectionsRequest
import com.mrdigitalpartners.oprah.web.dataTransferObjects.SectionUpdateRequest
import com.mrdigitalpartners.oprah.web.dataTransferObjects.SubmitSectionRequest
import com.mrdigitalpartners.oprah.web.dataTransferObjects.SubmitToPartnerRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForObject
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import java.nio.file.Files
import java.util.UUID

@Component
class WebHelper(
    @Autowired val restTemplate: TestRestTemplate,
    @Autowired val databaseHelper: DatabaseHelper
) {
    val coreHelper = CoreHelper()

    fun getQuestionnaires(partnerId: String) =
        restTemplate.getForObject<GetQuestionnairesResponse>("/questionnaires/partner/$partnerId")!!.questionnaires

    fun getQuestionnaire(questionnaireId: UUID) =
        restTemplate.getForObject<GetQuestionnaireResponse>("/questionnaires/$questionnaireId")!!.questionnaire

    fun createQuestionnaire(
        partnerId: String = "THE_HAIRY_BAKERS",
        sections: List<CreateSectionRequest> = listOf(createSectionRequest()),
        name: String = "Default Questionnaire",
        user: User = coreHelper.user()
    ): Questionnaire {
        postWithHeaders(
            "/questionnaires",
            CreateQuestionnaireRequest(partnerId, sections, name),
            usernameHeader(user.username)
        )
        return getQuestionnaires(partnerId).find { it.name == name }!!
    }

    fun createSectionRequest(
        title: String = "Default section Title",
        questions: List<TemplateQuestion> = listOf(coreHelper.templateQuestion())
    ): CreateSectionRequest {
        return if (questions.count() != 0) {
            questions.forEach { databaseHelper.saveTemplateQuestion(it.id, it.label) }
            CreateSectionRequest(title, questions.toList())
        } else {
            CreateSectionRequest(
                title,
                listOf(databaseHelper.saveTemplateQuestion(label = "Do you like french baguette?"))
            )
        }
    }

    fun getTemplates() = restTemplate.getForObject<GetTemplatesResponse>("/templates")

    fun mapTemplateToSectionsRequest(firstTemplate: TemplateQuestionnaire) =
        firstTemplate.sections.map { createSectionRequest(it.title, it.templateQuestions) }

    fun submitSection(
        questionnaireId: UUID,
        submitSectionRequest: SubmitSectionRequest,
        user: User = coreHelper.user()
    ) =
        postWithHeaders(
            "/questionnaires/$questionnaireId/submit-section",
            submitSectionRequest,
            usernameHeader(user.username)
        )

    fun resubmitSection(questionnaireId: UUID, sectionTitle: String) {
        val questionnaire = getQuestionnaire(questionnaireId)
        val section = questionnaire.sections.find { it.title == sectionTitle }
        val submitSectionsRequest = SubmitSectionRequest(section = section!!)
        submitSection(questionnaireId, submitSectionsRequest)
    }

    fun answerAllSectionQuestions(questionnaireId: UUID, sectionTitle: String) {
        answerAllSectionQuestionsWith(questionnaireId, sectionTitle, "I LOVE BREAD")
    }

    fun answerAllSectionQuestionsWith(questionnaireId: UUID, sectionTitle: String, answer: String) {
        val questionnaire = getQuestionnaire(questionnaireId)
        val submitSectionsRequest = answerAllQuestionsWithAnswer(answer, sectionTitle, questionnaire)
        submitSection(questionnaireId, submitSectionsRequest)
    }

    private fun answerAllQuestionsWithAnswer(
        answer: String,
        sectionTitle: String,
        questionnaire: Questionnaire
    ): SubmitSectionRequest {
        val section = questionnaire.sections.find { it.title == sectionTitle }!!
        return SubmitSectionRequest(section = coreHelper.answerAllQuestionsWith(answer, section))
    }

    fun assignMrdpUserToSection(
        questionnaireId: UUID,
        sectionTitle: String,
        username: String = "Gayle King (and the Mill)"
    ) =
        postWithHeaders<Void>(
            url = "/questionnaires/$questionnaireId/assign-user-to-section?sectionTitle=$sectionTitle",
            headers = usernameHeader(username)
        )

    fun approveSection(questionnaireId: UUID, username: String, sectionTitle: String) =
        postWithHeaders<Void>(
            url = "/questionnaires/$questionnaireId/approve-section?sectionTitle=$sectionTitle",
            headers = usernameHeader(username)
        )

    fun submitQuestionnaireToPartner(
        questionnaireId: UUID,
        submissionDueInWeeks: Int = 4,
        user: User = coreHelper.user()
    ) =
        postWithHeaders(
            url = "/questionnaires/$questionnaireId/submit-to-partner",
            body = SubmitToPartnerRequest(submissionDueInWeeks),
            headers = usernameHeader(user.username)
        )

    fun acknowledgeQuestionnaire(questionnaireId: UUID, user: User = coreHelper.user()) =
        postWithHeaders<Void>(
            url = "/questionnaires/$questionnaireId/acknowledge",
            headers = usernameHeader(user.username)
        )

    fun saveSectionsRequestWithAllQuestionsAnswered(answerToSave: String, questionnaire: Questionnaire) =
        SaveSectionsRequest(questionnaire.sections.map { section ->
            SectionUpdateRequest(
                section.title,
                section.questions.map { question -> QuestionUpdateRequest(question.id, answerToSave) })
        })

    fun saveSections(questionnaireId: UUID, saveSectionsRequest: SaveSectionsRequest, user: User = coreHelper.user()) =
        postWithHeaders(
            url = "/questionnaires/$questionnaireId/save-sections",
            body = saveSectionsRequest,
            headers = usernameHeader(user.username)
        )

    fun createReturnFirstSectionToPartnerRequest(
        questionnaireId: UUID,
        clarificationRequestMessage: String = "Please clarify"
    ): ReturnSectionToPartnerRequest {
        val questionnaire = getQuestionnaire(questionnaireId)
        val firstSection = questionnaire.sections[0]
        return ReturnSectionToPartnerRequest(
            coreHelper.requestClarificationOnAllQuestions(
                firstSection,
                clarificationRequestMessage
            )
        )
    }

    fun returnSectionToPartner(
        questionnaireId: UUID,
        request: ReturnSectionToPartnerRequest = createReturnFirstSectionToPartnerRequest(questionnaireId),
        user: User = coreHelper.user()
    ) =
        postWithHeaders(
            url = "/questionnaires/$questionnaireId/return-section-to-partner",
            body = request,
            headers = usernameHeader(user.username)
        )

    fun acknowledgeReturnedSections(questionnaireId: UUID, user: User = coreHelper.user()) =
        postWithHeaders<Void>(
            url = "/questionnaires/$questionnaireId/acknowledge-returned-sections",
            headers = usernameHeader(user.username)
        )

    fun acknowledgeResubmittedSections(questionnaireId: UUID, currentUser: String) =
        postWithHeaders<Void>(
            url = "/questionnaires/$questionnaireId/acknowledge-resubmitted-sections",
            headers = usernameHeader(currentUser)
        )

    private fun <Body> postWithHeaders(
        url: String,
        body: Body? = null,
        headers: List<Pair<String, String>> = emptyList()
    ): ResponseEntity<String> {
        val httpHeaders = HttpHeaders()
        headers.forEach { headerPair -> httpHeaders.add(headerPair.first, headerPair.second) }
        val request = HttpEntity(body, httpHeaders)
        return restTemplate.postForEntity(url, request)
    }

    private fun usernameHeader(username: String) = listOf(Pair("username", username))

    // Supporting Documents

    fun saveSupportingDocument(
        fileContents: String = "Default test file contents",
        sectionQuestionId: UUID = UUID.randomUUID(),
        providedAsRequired: Boolean = true,
        user: User = coreHelper.user()
    ): UUID {
        val headers = usernameHeader(user.username) + Pair("Content-Type", "multipart/form-data")
        val fileBody = supportingDocumentRequestBody(fileContents, sectionQuestionId, providedAsRequired)
        val response = postWithHeaders("/supporting-documents", fileBody, headers)
        return extractUuidFromResponseBody(response.body!!)
    }

    private fun supportingDocumentRequestBody(
        fileContents: String,
        sectionQuestionId: UUID,
        providedAsRequired: Boolean
    ): LinkedMultiValueMap<String, Any> {
        val body = LinkedMultiValueMap<String, Any>()
        body.add("file", testDocument(fileContents))
        body.add("sectionQuestionId", sectionQuestionId.toString())
        body.add("providedAsRequired", providedAsRequired)
        return body
    }

    private fun extractUuidFromResponseBody(response: String) =
        UUID.fromString(response.replace("\"", ""))

    private fun testDocument(contents: String): Resource {
        val testFile = Files.createTempFile("test-file", ".txt")
        Files.write(testFile, contents.toByteArray())
        return FileSystemResource(testFile.toFile())
    }
}
