package com.mrdigitalpartners.oprah.web

import com.mrdigitalpartners.oprah.persistence.DatabaseHelper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TemplatesControllerTest(@Autowired val webHelper: WebHelper, @Autowired val databaseHelper: DatabaseHelper) {

    @AfterEach
    fun teardown() {
        databaseHelper.teardown()
    }

    @Test
    fun `finds the hardcoded templates (a number of spot checks are annotated)`() {
        val templatesResponse = webHelper.getTemplates()

        // there should be 4 hardcoded templates
        assertEquals(4, templatesResponse!!.templates.count())

        // The sections and questions should be in a certain order (only checking one template)
        assertEquals(
                listOf("General/Admin", "Finance", "Commercial", "Compliance", "Tech", "Operations", "Claims"),
                templatesResponse.templates[3].sections.map { it.title }
        )
        assertEquals(listOf(
                "Agent Name?",
                "Legal Name?",
                "Trading Name(s)",
                "Co. Licence & Registration No.",
                "Date of Completion",
                "Completion Signatory",
                "Position in Company"
        ), templatesResponse.templates[0].sections[0].templateQuestions.map { it.label })

        // The supporting documents requirements should be hardcoded (only checking a couple)
        assertFalse(templatesResponse.templates[1].sections[2].templateQuestions[5].requiresSupportingDocuments)
        assertTrue(templatesResponse.templates[1].sections[2].templateQuestions[6].requiresSupportingDocuments)
    }

    @Test
    fun `creates a questionnaire from a hardcoded template`() {
        val partnerId = "BBM"
        val questionnaireName = "Questionnaire 1"
        val firstTemplate = webHelper.getTemplates()!!.templates.first()
        val sectionsRequest = webHelper.mapTemplateToSectionsRequest(firstTemplate)

        val id = webHelper.createQuestionnaire(partnerId, sectionsRequest, questionnaireName).id

        val questionnaire = webHelper.getQuestionnaire(id)
        assertEquals(questionnaireName, questionnaire.name)
        assertEquals(firstTemplate.sections.count(), questionnaire.sections.count())
        assertEquals(firstTemplate.sections.first().templateQuestions, questionnaire.sections.first().questions.map { it.templateQuestion })
    }
}
