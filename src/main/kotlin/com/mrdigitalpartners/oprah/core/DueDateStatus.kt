package com.mrdigitalpartners.oprah.core

import java.time.ZonedDateTime

enum class DueStatus {
    SUBMISSION_DUE, APPROVAL_DUE, APPROVED
}

data class DueDateStatus(
    val at: ZonedDateTime,
    val dueStatus: DueStatus
)
