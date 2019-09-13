package com.mrdigitalpartners.oprah.core.questionnaireLifecycle

import com.mrdigitalpartners.oprah.core.Questionnaire
import com.mrdigitalpartners.oprah.core.Section
import com.mrdigitalpartners.oprah.core.SectionStatus
import com.tinder.StateMachine

sealed class StateMachineSectionStatus {
    data class NotStarted(val sectionTitle: String) : StateMachineSectionStatus()
    data class InProgress(val sectionTitle: String) : StateMachineSectionStatus()
    data class Submitted(val sectionTitle: String) : StateMachineSectionStatus()
    data class UnderReview(val sectionTitle: String) : StateMachineSectionStatus()
    data class Returned(val sectionTitle: String) : StateMachineSectionStatus()
    object Approved : StateMachineSectionStatus()
}

fun initialSectionStateMachine(initialSectionStatus: StateMachineSectionStatus) = StateMachine.create<StateMachineSectionStatus, Action, Void> {
    initialState(initialSectionStatus)

    state<StateMachineSectionStatus.NotStarted> {
        on<Action.AllSections.QuestionnaireAcknowledgedByPartner> {
            transitionTo(StateMachineSectionStatus.InProgress(sectionTitle))
        }

        on<Action.AllSections.SubmitToPartner> {
            transitionTo(StateMachineSectionStatus.NotStarted(sectionTitle))
        }
    }

    state<StateMachineSectionStatus.InProgress> {
        on<Action.SingleSection.SubmitSection> { submitSections ->
            if (submitSections.sectionToSubmit.title == sectionTitle) {
                transitionTo(StateMachineSectionStatus.Submitted(sectionTitle))
            } else {
                transitionTo(StateMachineSectionStatus.InProgress(sectionTitle))
            }
        }

        on<Action.SomeSections.SaveSections> {
            transitionTo(StateMachineSectionStatus.InProgress(sectionTitle))
        }
    }

    state<StateMachineSectionStatus.Submitted> {
        on<Action.SingleSection.AssignUserToSection> { assignUserToSection ->
            if (assignUserToSection.sectionTitle == sectionTitle) {
                transitionTo(StateMachineSectionStatus.UnderReview(sectionTitle))
            } else {
                transitionTo(StateMachineSectionStatus.Submitted(sectionTitle))
            }
        }

        on<Action.SomeSections.AcknowledgeReSubmittedSections> {
            transitionTo(StateMachineSectionStatus.UnderReview(sectionTitle))
        }
    }

    state<StateMachineSectionStatus.UnderReview> {
        on<Action.SingleSection.ApproveSection> { approveSection ->
            if (approveSection.sectionTitle == sectionTitle) {
                transitionTo(StateMachineSectionStatus.Approved)
            } else {
                transitionTo(StateMachineSectionStatus.UnderReview(sectionTitle))
            }
        }

        on<Action.SingleSection.ReturnSectionToPartner> {
            transitionTo(StateMachineSectionStatus.Returned(sectionTitle))
        }
    }

    state<StateMachineSectionStatus.Returned> {
        on<Action.SomeSections.AcknowledgeReturnedSections> {
            transitionTo(StateMachineSectionStatus.InProgress(sectionTitle))
        }
    }

    state<StateMachineSectionStatus.Approved> {}
}

fun updateSectionStatuses(action: Action, questionnaire: Questionnaire): Questionnaire {
    return questionnaire.copy(sections = questionnaire.sections.map { section ->
        when (action) {
            is Action.AllSections ->
                updateSectionStatus(section, action)
            is Action.SomeSections ->
                if (action.sectionTitles.contains(section.title)) {
                    updateSectionStatus(section, action)
                } else {
                    section
                }
            is Action.SingleSection ->
                if (action.sectionTitle.contains(section.title)) {
                    updateSectionStatus(section, action)
                } else {
                    section
                }
        }
    })
}

private fun updateSectionStatus(section: Section, action: Action): Section {
    val sectionStateMachine = initialSectionStateMachine(toStateMachineSectionStatus(section.status, section.title))
    val transitionResult = sectionStateMachine.transition(action)
    if (transitionResult is StateMachine.Transition.Invalid) {
        throw QuestionnaireLifecycleException(
            "Invalid section transition: status ${section.status} -> tried to perform ${action::class.simpleName}"
        )
    }
    return section.copy(status = fromStateMachineSectionStatus(sectionStateMachine.state))
}

private fun toStateMachineSectionStatus(status: SectionStatus, title: String): StateMachineSectionStatus =
    when (status) {
        SectionStatus.NOT_STARTED -> StateMachineSectionStatus.NotStarted(title)
        SectionStatus.IN_PROGRESS -> StateMachineSectionStatus.InProgress(title)
        SectionStatus.SUBMITTED -> StateMachineSectionStatus.Submitted(title)
        SectionStatus.UNDER_REVIEW -> StateMachineSectionStatus.UnderReview(title)
        SectionStatus.RETURNED -> StateMachineSectionStatus.Returned(title)
        SectionStatus.APPROVED -> StateMachineSectionStatus.Approved
    }

private fun fromStateMachineSectionStatus(status: StateMachineSectionStatus): SectionStatus =
    when (status) {
        is StateMachineSectionStatus.NotStarted -> SectionStatus.NOT_STARTED
        is StateMachineSectionStatus.InProgress -> SectionStatus.IN_PROGRESS
        is StateMachineSectionStatus.Submitted -> SectionStatus.SUBMITTED
        is StateMachineSectionStatus.UnderReview -> SectionStatus.UNDER_REVIEW
        is StateMachineSectionStatus.Returned -> SectionStatus.RETURNED
        is StateMachineSectionStatus.Approved -> SectionStatus.APPROVED
    }
