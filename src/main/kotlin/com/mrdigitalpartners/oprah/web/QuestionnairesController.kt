package com.mrdigitalpartners.oprah.web

import com.mrdigitalpartners.oprah.core.Questionnaire
import com.mrdigitalpartners.oprah.core.User
import com.mrdigitalpartners.oprah.core.questionnaireLifecycle.Action
import com.mrdigitalpartners.oprah.core.questionnaireLifecycle.LifecycleService
import com.mrdigitalpartners.oprah.persistence.QuestionnairesRepository
import com.mrdigitalpartners.oprah.persistence.changes
import com.mrdigitalpartners.oprah.utils.pipe
import com.mrdigitalpartners.oprah.web.dataTransferObjects.CreateQuestionnaireRequest
import com.mrdigitalpartners.oprah.web.dataTransferObjects.GetQuestionnaireResponse
import com.mrdigitalpartners.oprah.web.dataTransferObjects.GetQuestionnairesResponse
import com.mrdigitalpartners.oprah.web.dataTransferObjects.ReturnSectionToPartnerRequest
import com.mrdigitalpartners.oprah.web.dataTransferObjects.SaveSectionsRequest
import com.mrdigitalpartners.oprah.web.dataTransferObjects.SubmitSectionRequest
import com.mrdigitalpartners.oprah.web.dataTransferObjects.SubmitToPartnerRequest
import com.mrdigitalpartners.oprah.web.dataTransferObjects.newQuestionnaireFromRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import com.mrdigitalpartners.oprah.core.questionnaireLifecycle.Action.AllSections as MultiSectionAction

@RestController
@CrossOrigin
@RequestMapping("/questionnaires")
class QuestionnairesController(@Autowired val questionnairesRepository: QuestionnairesRepository) {

    private val questionnaireLifecycleService = LifecycleService()
    private val actionBuilder = ActionBuilder()

    @PostMapping
    fun createQuestionnaire(
        @RequestBody request: CreateQuestionnaireRequest,
        @RequestHeader("username") username: String
    ) {
        questionnairesRepository.save(request.partnerId, newQuestionnaireFromRequest(request), User(username))
    }

    @GetMapping("/partner/{partnerId}")
    fun getQuestionnaires(@PathVariable("partnerId") partnerId: String): GetQuestionnairesResponse {
        return GetQuestionnairesResponse(questionnairesRepository.findAll(partnerId))
    }

    @GetMapping("/{questionnaireId}")
    fun getQuestionnaire(@PathVariable("questionnaireId") questionnaireId: UUID): GetQuestionnaireResponse {
        return GetQuestionnaireResponse(questionnairesRepository.find(questionnaireId))
    }

    @PostMapping("/{questionnaireId}/submit-to-partner")
    fun submitToPartner(
        @PathVariable("questionnaireId") questionnaireId: UUID,
        @RequestBody submitToPartnerRequest: SubmitToPartnerRequest,
        @RequestHeader("username") username: String
    ) {
        runUpdateAction(
            questionnaireId,
            User(username)
        ) { MultiSectionAction.SubmitToPartner(submitToPartnerRequest.submissionDueInWeeks) }
    }

    @PostMapping("/{questionnaireId}/acknowledge")
    fun acknowledgeQuestionnaire(
        @PathVariable("questionnaireId") questionnaireId: UUID,
        @RequestHeader("username") username: String
    ) {
        runUpdateAction(questionnaireId, User(username)) { MultiSectionAction.QuestionnaireAcknowledgedByPartner }
    }

    @PostMapping("/{questionnaireId}/save-sections")
    fun saveSections(
        @PathVariable("questionnaireId") questionnaireId: UUID,
        @RequestBody saveSectionRequest: SaveSectionsRequest,
        @RequestHeader("username") username: String
    ) {
        runUpdateAction(questionnaireId, User(username)) { actionBuilder.saveSections(saveSectionRequest) }
    }

    @PostMapping("/{questionnaireId}/submit-section")
    fun submitSection(
        @PathVariable("questionnaireId") questionnaireId: UUID,
        @RequestBody submitSectionRequest: SubmitSectionRequest,
        @RequestHeader("username") username: String
    ) {
        runUpdateAction(questionnaireId, User(username)) {
            Action.SingleSection.SubmitSection(
                submitSectionRequest.section.title,
                submitSectionRequest.section
            )
        }
    }

    @PostMapping("/{questionnaireId}/assign-user-to-section")
    fun assignUserToSection(
        @PathVariable("questionnaireId") questionnaireId: UUID,
        @RequestParam("sectionTitle") sectionTitle: String,
        @RequestHeader("username") username: String
    ) {
        runUpdateAction(questionnaireId, User(username)) {
            Action.SingleSection.AssignUserToSection(
                sectionTitle,
                username
            )
        }
    }

    @PostMapping("/{questionnaireId}/return-section-to-partner")
    fun returnSectionToPartner(
        @PathVariable("questionnaireId") questionnaireId: UUID,
        @RequestBody returnSectionToPartnerRequest: ReturnSectionToPartnerRequest,
        @RequestHeader("username") username: String
    ) {
        runUpdateAction(questionnaireId, User(username)) {
            Action.SingleSection.ReturnSectionToPartner(
                returnSectionToPartnerRequest.section.title,
                returnSectionToPartnerRequest.section
            )
        }
    }

    @PostMapping("/{questionnaireId}/approve-section")
    fun approveSection(
        @PathVariable("questionnaireId") questionnaireId: UUID,
        @RequestParam("sectionTitle") sectionTitle: String,
        @RequestHeader("username") username: String
    ) {
        runUpdateAction(questionnaireId, User(username)) { Action.SingleSection.ApproveSection(sectionTitle) }
    }

    @PostMapping("/{questionnaireId}/acknowledge-returned-sections")
    fun acknowledgeReturnedSections(
        @PathVariable("questionnaireId") questionnaireId: UUID,
        @RequestHeader("username") username: String
    ) {
        runUpdateAction(questionnaireId, User(username)) { questionnaire ->
            actionBuilder.acknowledgeReturnedSections(
                questionnaire
            )
        }
    }

    @PostMapping("/{questionnaireId}/acknowledge-resubmitted-sections")
    fun acknowledgeResubmittedSections(
        @PathVariable("questionnaireId") questionnaireId: UUID,
        @RequestHeader("username") currentUser: String
    ) {
        runUpdateAction(
            questionnaireId,
            User(currentUser)
        ) { questionnaire -> actionBuilder.acknowledgeResubmittedSections(questionnaire, currentUser) }
    }

    private fun runUpdateAction(questionnaireId: UUID, createdBy: User, generateAction: (Questionnaire) -> Action) {
        val currentQuestionnaire = questionnairesRepository.find(questionnaireId)
        generateAction(currentQuestionnaire)
            .pipe { action -> questionnaireLifecycleService.process(action, currentQuestionnaire) }
            .pipe { updatedQuestionnaire -> changes(currentQuestionnaire, updatedQuestionnaire) }
            .pipe { changes -> questionnairesRepository.update(changes, createdBy) }
    }
}
