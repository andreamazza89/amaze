package com.mrdigitalpartners.oprah.core

import com.mrdigitalpartners.oprah.core.questionnaireLifecycle.Action
import com.mrdigitalpartners.oprah.web.ActionBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LifecycleActionBuilderTest {

    private val coreHelper = CoreHelper()

    @Test
    fun `for any status other than submitted, sections do not need acknowledging re-submission `() {
        val currentUser = "Crusty mac Rolls"
        val questionnaire = coreHelper.inProgressQuestionnaire(sections = *arrayOf(
            coreHelper.inProgressSection(assignedTo = null),
            coreHelper.underReviewSection(assignedTo = currentUser)
        ))

        val action = ActionBuilder().acknowledgeResubmittedSections(questionnaire, currentUser)

        assertEquals(Action.SomeSections.AcknowledgeReSubmittedSections(emptyList()), action)
    }

    @Test
    fun `does not need acknowledge resubmission if section is not assigned to current user`() {
        val currentUser = "Crusty mac Rolls"
        val questionnaire = coreHelper.inProgressQuestionnaire(sections = *arrayOf(
            coreHelper.submittedSection(assignedTo = null),
            coreHelper.submittedSection(assignedTo = "Someone Else")
        ))

        val action = ActionBuilder().acknowledgeResubmittedSections(questionnaire, currentUser)

        assertEquals(Action.SomeSections.AcknowledgeReSubmittedSections(emptyList()), action)
    }

    @Test
    fun `needs acknowledgement if section is submitted and assigned to the current user`() {
        val currentUser = "Crusty mac Rolls"
        val sectionNeedingAcknowledgement = coreHelper.submittedSection(assignedTo = currentUser)
        val questionnaire = coreHelper.inProgressQuestionnaire(sections = *arrayOf(
            sectionNeedingAcknowledgement,
            coreHelper.submittedSection(assignedTo = "Someone Else")
        ))

        val action = ActionBuilder().acknowledgeResubmittedSections(questionnaire, currentUser)

        assertEquals(
            Action.SomeSections.AcknowledgeReSubmittedSections(listOf(sectionNeedingAcknowledgement.title)),
            action
        )
    }
}
