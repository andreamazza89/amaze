package com.mrdigitalpartners.oprah.persistence

import com.mrdigitalpartners.oprah.core.DueDateStatus
import com.mrdigitalpartners.oprah.core.DueStatus
import com.mrdigitalpartners.oprah.core.SectionStatus
import com.mrdigitalpartners.oprah.utils.addWeeks
import com.mrdigitalpartners.oprah.utils.truncatedToDays
import java.time.ZonedDateTime

typealias SectionStatusTimes = List<List<SectionStatusTime>>

data class SectionStatusTime(
    val status: SectionStatus,
    val time: ZonedDateTime
)

fun toQuestionnaireDueDateStatus(
    submissionDueInWeeks: Int?,
    approvalDueInWeeks: Int? = 4,
    utcTimeNow: ZonedDateTime,
    statusTimes: SectionStatusTimes
): DueDateStatus? {
    return when {
        statusTimes.allApproved() -> toApprovedOn(statusTimes)
        statusTimes.allSubmittedOnce() -> toApprovalDue(statusTimes, approvalDueInWeeks!!)
        submissionDueInWeeks != null -> DueDateStatus(utcTimeNow.truncatedToDays().addWeeks(submissionDueInWeeks), DueStatus.SUBMISSION_DUE)
        else -> null
    }
}

fun toApprovedOn(statusTimes: List<List<SectionStatusTime>>): DueDateStatus {
    val approvedOn = statusTimes
        .map { it.filter { statusTime -> statusTime.status == SectionStatus.APPROVED } }
        .map { it.minBy { statusTime -> statusTime.time } }
        .maxBy { it!!.time }!!
        .time

    return DueDateStatus(approvedOn.truncatedToDays(), DueStatus.APPROVED)
}

private fun SectionStatusTimes.allApproved(): Boolean =
    this.all { it.any { statusTime -> statusTime.status == SectionStatus.APPROVED } }

private fun SectionStatusTimes.allSubmittedOnce(): Boolean =
    this.all { sectionStatuses -> sectionStatuses.any { it.status == SectionStatus.SUBMITTED } }

fun toApprovalDue(statusTimes: SectionStatusTimes, approvalDueInWeeks: Int): DueDateStatus {
    val submittedAllSectionsOnceAt = statusTimes
        .map { it.filter { statusTime -> statusTime.status == SectionStatus.SUBMITTED } }
        .map { it.minBy { statusTime -> statusTime.time } }
        .maxBy { it!!.time }!!
        .time

    return DueDateStatus(
        submittedAllSectionsOnceAt.truncatedToDays().addWeeks(approvalDueInWeeks),
        DueStatus.APPROVAL_DUE
    )
}
