package com.mrdigitalpartners.oprah.web

import com.mrdigitalpartners.oprah.core.SupportingDocument
import com.mrdigitalpartners.oprah.core.User
import com.mrdigitalpartners.oprah.persistence.QuestionnairesRepository
import com.mrdigitalpartners.oprah.persistence.SupportingDocumentsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@ConditionalOnProperty(prefix = "controller", name = ["allow-dangerous-endpoints"])
class TestingOnlyController(
    @Autowired val questionnairesRepository: QuestionnairesRepository,
    @Autowired val supportingDocumentsRepository: SupportingDocumentsRepository
) {

    @PostMapping("/teardown")
    fun teardown() {
        questionnairesRepository.deleteAll()
    }

    @PostMapping("/create-supporting-document-for-testing")
    fun createSupportingDocument(
        @RequestParam sectionQuestionId: String,
        providedAsRequired: Boolean
    ) {
        val testData = "File auto-generated from the test controller for question $sectionQuestionId".toByteArray()
        val supportingDocument = SupportingDocument(
            id = UUID.randomUUID(),
            fileReference = UUID.randomUUID(),
            providedAsRequired = providedAsRequired,
            fileName = "test file name for question $sectionQuestionId",
            fileType = "text/plain"
        )
        supportingDocumentsRepository.save(
            testData,
            UUID.fromString(sectionQuestionId),
            supportingDocument,
            User("Default username for testing-only supporting document upload")
        )
    }
}
