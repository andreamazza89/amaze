package com.mrdigitalpartners.oprah.persistence

import com.mrdigitalpartners.oprah.core.DueDateStatus
import com.mrdigitalpartners.oprah.core.DueStatus
import com.mrdigitalpartners.oprah.core.SectionStatus
import com.mrdigitalpartners.oprah.utils.addWeeks
import com.mrdigitalpartners.oprah.utils.truncatedToDays
import com.mrdigitalpartners.oprah.utils.utcTimeNow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class QuestionnaireDueDateDetailsTest {

    private val notStartedTimes = listOf(listOf(notStartedTime()))

    @Test
    fun `there are no due dates when the questionnaire is new`() {
        assertNull(
            toQuestionnaireDueDateStatus(
                submissionDueInWeeks = null,
                utcTimeNow = utcTimeNow(),
                statusTimes = notStartedTimes
            )
        )
    }

    @Test
    fun `calculates submission due date`() {
        val now = utcTimeNow()
        val dueInWeeks = 4

        val dueDate = toQuestionnaireDueDateStatus(
            submissionDueInWeeks = dueInWeeks,
            utcTimeNow = now,
            statusTimes = notStartedTimes
        )

        assertEquals(
            DueDateStatus(now.truncatedToDays().addWeeks(dueInWeeks), DueStatus.SUBMISSION_DUE),
            dueDate
        )
    }

    @Test
    fun `calculates submission due date when not all sections are submitted`() {
        val now = utcTimeNow()
        val submissionDueInWeeks = 4
        val sectionStatusTimes =
            listOf(
                listOf(submittedTime()),
                listOf(inProgressTime())
            )

        val dueDate = toQuestionnaireDueDateStatus(
            submissionDueInWeeks = submissionDueInWeeks,
            utcTimeNow = now,
            statusTimes = sectionStatusTimes
        )

        assertEquals(
            DueDateStatus(now.truncatedToDays().addWeeks(submissionDueInWeeks), DueStatus.SUBMISSION_DUE),
            dueDate
        )
    }

    @Test
    fun `calculates approval due date from the earliest submitted time found across sections`() {
        val time1 = utcTimeNow()
        val time2 = utcTimeNow().plusDays(2)
        val time3 = utcTimeNow().plusDays(3)

        val submissionDueInWeeks = 4
        val approvalDueInWeeks = 2
        val sectionStatusTimes =
            listOf(
                listOf(notStartedTime(), inProgressTime(), submittedTime(time1), submittedTime(time3)),
                listOf(notStartedTime(), inProgressTime(), submittedTime(time2))
            )

        val dueDateStatus = toQuestionnaireDueDateStatus(
            submissionDueInWeeks = submissionDueInWeeks,
            approvalDueInWeeks = approvalDueInWeeks,
            utcTimeNow = utcTimeNow(),
            statusTimes = sectionStatusTimes
        )

        assertEquals(
            DueDateStatus(time2.truncatedToDays().addWeeks(approvalDueInWeeks), DueStatus.APPROVAL_DUE),
            dueDateStatus
        )
    }

    @Test
    fun `is APPROVED_ON when all sections have been approved`() {
        val time1 = utcTimeNow()
        val time2 = utcTimeNow().plusDays(2)
        val time3 = utcTimeNow().plusDays(3)

        val sectionStatusTimes =
            listOf(
                listOf(notStartedTime(), inProgressTime(), submittedTime(time1), approvedTime(time2)),
                listOf(notStartedTime(), inProgressTime(), submittedTime(time1), approvedTime(time3)),
                listOf(notStartedTime(), inProgressTime(), submittedTime(time1), approvedTime(time2))
            )

        val dueDateStatus = toQuestionnaireDueDateStatus(
            submissionDueInWeeks = 42,
            utcTimeNow = utcTimeNow(),
            statusTimes = sectionStatusTimes
        )

        assertEquals(
            DueDateStatus(time3.truncatedToDays(), DueStatus.APPROVED),
            dueDateStatus
        )
    }

    private fun notStartedTime(time: ZonedDateTime = utcTimeNow()): SectionStatusTime =
        statusTime(SectionStatus.NOT_STARTED, time)

    private fun inProgressTime(time: ZonedDateTime = utcTimeNow()): SectionStatusTime =
        statusTime(SectionStatus.IN_PROGRESS, time)

    private fun submittedTime(time: ZonedDateTime = utcTimeNow()): SectionStatusTime =
        statusTime(SectionStatus.SUBMITTED, time)

    private fun approvedTime(time: ZonedDateTime = utcTimeNow()): SectionStatusTime =
        statusTime(SectionStatus.APPROVED, time)

    private fun statusTime(sectionStatus: SectionStatus, time: ZonedDateTime = utcTimeNow()) =
        SectionStatusTime(status = sectionStatus, time = time)
}
