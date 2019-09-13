package com.mrdigitalpartners.oprah.web

import com.mrdigitalpartners.oprah.core.CoreHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SupportingDocumentsControllerTest(@Autowired val webHelper: WebHelper) {
    private val coreHelper = CoreHelper()

    @Test
    fun `partner can upload optional and required supporting documents`() {
        val questionRequiringSupportingDocuments = coreHelper.templateQuestion(requiresSupportingDocuments = true)
        val sectionRequest = webHelper.createSectionRequest(questions = listOf(questionRequiringSupportingDocuments))
        val questionnaire = webHelper.createQuestionnaire(sections = listOf(sectionRequest))

        val documentOneId = webHelper.saveSupportingDocument(
            sectionQuestionId = questionnaire.sections[0].questions[0].id,
            providedAsRequired = false
        )
        val documentTwoId = webHelper.saveSupportingDocument(
            sectionQuestionId = questionnaire.sections[0].questions[0].id,
            providedAsRequired = true
        )

        val supportingDocumentsFound =
            webHelper.getQuestionnaire(questionnaire.id).sections[0].questions[0].supportingDocuments
        assertEquals(documentOneId, supportingDocumentsFound[0].fileReference)
        assertFalse(supportingDocumentsFound[0].providedAsRequired)

        assertEquals(documentTwoId, supportingDocumentsFound[1].fileReference)
        assertTrue(supportingDocumentsFound[1].providedAsRequired)
    }
}
