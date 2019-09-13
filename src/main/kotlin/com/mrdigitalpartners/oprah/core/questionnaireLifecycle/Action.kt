package com.mrdigitalpartners.oprah.core.questionnaireLifecycle

import com.mrdigitalpartners.oprah.core.QuestionUpdate
import com.mrdigitalpartners.oprah.core.Section

sealed class Action {
    sealed class AllSections : Action() {
        data class SubmitToPartner(
            val submissionDueInWeeks: Int
        ) : AllSections()

        object QuestionnaireAcknowledgedByPartner : AllSections()
    }

    sealed class SomeSections : Action() {
        abstract val sectionTitles: List<String>

        data class SaveSections(
            override val sectionTitles: List<String>,
            val newAnswers: List<QuestionUpdate<String>>
        ) : SomeSections()

        data class AcknowledgeReturnedSections(
            override val sectionTitles: List<String>
        ) : SomeSections()

        data class AcknowledgeReSubmittedSections(
            override val sectionTitles: List<String>
        ) : SomeSections()
    }

    sealed class SingleSection : Action() {
        abstract val sectionTitle: String

        data class SubmitSection(
            override val sectionTitle: String,
            val sectionToSubmit: Section
        ) : SingleSection()

        data class AssignUserToSection(
            override val sectionTitle: String,
            val username: String
        ) : SingleSection()

        data class ReturnSectionToPartner(
            override val sectionTitle: String,
            val sectionToReturn: Section
        ) : SingleSection()

        data class ApproveSection(
            override val sectionTitle: String
        ) : SingleSection()
    }
}
