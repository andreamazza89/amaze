package com.mrdigitalpartners.oprah.core.questionnaireLifecycle

import com.mrdigitalpartners.oprah.core.Questionnaire
import com.mrdigitalpartners.oprah.core.QuestionnaireStatus
import com.mrdigitalpartners.oprah.core.Section
import com.mrdigitalpartners.oprah.core.SectionStatus
import com.tinder.StateMachine

sealed class StateMachineQuestionnaireStatus {
    object New : StateMachineQuestionnaireStatus()
    object NotStarted : StateMachineQuestionnaireStatus()
    object InProgress : StateMachineQuestionnaireStatus()
    object Approved : StateMachineQuestionnaireStatus()
}

fun initialQuestionnaireStateMachine(
    initialQuestionnaireStatus: StateMachineQuestionnaireStatus,
    sections: List<Section>
) = StateMachine.create<StateMachineQuestionnaireStatus, Action, Void> {
    initialState(initialQuestionnaireStatus)

    state<StateMachineQuestionnaireStatus.New> {
        on<Action.AllSections.SubmitToPartner> {
            transitionTo(StateMachineQuestionnaireStatus.NotStarted)
        }
    }

    state<StateMachineQuestionnaireStatus.NotStarted> {
        on<Action.AllSections.QuestionnaireAcknowledgedByPartner> {
            transitionTo(StateMachineQuestionnaireStatus.InProgress)
        }
    }

    state<StateMachineQuestionnaireStatus.InProgress> {
        on<Action.SingleSection.SubmitSection> {
            transitionTo(StateMachineQuestionnaireStatus.InProgress)
        }

        on<Action.SingleSection.AssignUserToSection> {
            transitionTo(StateMachineQuestionnaireStatus.InProgress)
        }

        on<Action.SomeSections.SaveSections> {
            transitionTo(StateMachineQuestionnaireStatus.InProgress)
        }

        on<Action.SingleSection.ReturnSectionToPartner> {
            transitionTo(StateMachineQuestionnaireStatus.InProgress)
        }

        on<Action.SomeSections.AcknowledgeReturnedSections> {
            transitionTo(StateMachineQuestionnaireStatus.InProgress)
        }

        on<Action.SomeSections.AcknowledgeReSubmittedSections> {
            transitionTo(StateMachineQuestionnaireStatus.InProgress)
        }

        on<Action.SingleSection.ApproveSection> {
            if (sections.all { it.status == SectionStatus.APPROVED }) {
                transitionTo(StateMachineQuestionnaireStatus.Approved)
            } else {
                transitionTo(StateMachineQuestionnaireStatus.InProgress)
            }
        }
    }

    state<StateMachineQuestionnaireStatus.Approved> {}
}

fun updateQuestionnaireStatus(action: Action, questionnaire: Questionnaire): Questionnaire {
    val questionnaireStateMachine =
        initialQuestionnaireStateMachine(
            toStateMachineQuestionnaireStatus(questionnaire.status),
            questionnaire.sections
        )
    val transitionResult = questionnaireStateMachine.transition(action)
    if (transitionResult is StateMachine.Transition.Invalid) {
        throw QuestionnaireLifecycleException(
            "Invalid questionnaire transition: status ${questionnaire.status} -> tried to perform ${action::class.simpleName}"
        )
    }
    return questionnaire.copy(status = fromStateMachineQuestionnaireStatus(
        questionnaireStateMachine.state
    )
    )
}

private fun toStateMachineQuestionnaireStatus(status: QuestionnaireStatus): StateMachineQuestionnaireStatus =
    when (status) {
        QuestionnaireStatus.NEW -> StateMachineQuestionnaireStatus.New
        QuestionnaireStatus.NOT_STARTED -> StateMachineQuestionnaireStatus.NotStarted
        QuestionnaireStatus.IN_PROGRESS -> StateMachineQuestionnaireStatus.InProgress
        QuestionnaireStatus.APPROVED -> StateMachineQuestionnaireStatus.Approved
    }

private fun fromStateMachineQuestionnaireStatus(status: StateMachineQuestionnaireStatus): QuestionnaireStatus =
    when (status) {
        StateMachineQuestionnaireStatus.New -> QuestionnaireStatus.NEW
        StateMachineQuestionnaireStatus.NotStarted -> QuestionnaireStatus.NOT_STARTED
        StateMachineQuestionnaireStatus.InProgress -> QuestionnaireStatus.IN_PROGRESS
        StateMachineQuestionnaireStatus.Approved -> QuestionnaireStatus.APPROVED
    }
