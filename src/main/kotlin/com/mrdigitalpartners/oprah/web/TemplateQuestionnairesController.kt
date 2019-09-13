package com.mrdigitalpartners.oprah.web

import com.mrdigitalpartners.oprah.core.TemplateQuestionnaire
import com.mrdigitalpartners.oprah.persistence.QuestionnaireTemplatesRepository
import com.mrdigitalpartners.oprah.web.dataTransferObjects.GetTemplatesResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
class TemplateQuestionnairesController(
    @Autowired val questionnaireTemplatesRepository: QuestionnaireTemplatesRepository
) {

    @Value("\${spring-profiles-active}")
    lateinit var environment: String

    @GetMapping("/templates")
    fun templates(): GetTemplatesResponse {
        val allTemplates = questionnaireTemplatesRepository.findAll()
        return GetTemplatesResponse(removeDemoTemplateWhenRunningInProduction(environment, allTemplates))
    }

    private fun removeDemoTemplateWhenRunningInProduction(
        environment: String,
        allTemplates: List<TemplateQuestionnaire>
    ): List<TemplateQuestionnaire> =
        if (environment == "production") {
            allTemplates.filter { it.name != "Demo Template" }
        } else {
            allTemplates
        }
}
