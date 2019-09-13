package com.mrdigitalpartners.oprah.persistence

import com.mrdigitalpartners.oprah.core.QuestionnaireStatus
import com.mrdigitalpartners.oprah.core.ReviewStatus
import com.mrdigitalpartners.oprah.core.SectionStatus
import org.springframework.data.repository.CrudRepository
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.OrderBy
import javax.persistence.Table

@Entity
@Table(name = "questionnaires", schema = "questionnaire")
data class DbQuestionnaire(
    @Id
    val id: UUID,

    val partnerId: String,

    val name: String,

    @OrderBy("created_at DESC")
    @OneToMany(mappedBy = "questionnaire")
    val statuses: List<DbQuestionnaireStatus> = emptyList(),

    @OneToOne(mappedBy = "questionnaire", orphanRemoval = true)
    @JoinColumn(name = "questionnaire_id")
    val submissionDueInWeeks: DbSubmissionDue? = null,

    @OrderBy("display_order ASC")
    @OneToMany(mappedBy = "questionnaire")
    val sections: List<DbSection> = emptyList(),

    val createdByUsername: String
) {
    fun submittedToPartnerAt() = this.statuses.findLast { it.status == QuestionnaireStatus.NOT_STARTED }
        ?.createdAt
        ?.toLocalDateTime()?.atZone(ZoneId.of("UTC"))
}

interface QuestionnaireDbInteractor : CrudRepository<DbQuestionnaire, UUID> {
    fun findAllByPartnerId(partnerId: String): Iterable<DbQuestionnaire>
}

@Entity
@Table(name = "questionnaire_statuses", schema = "questionnaire")
data class DbQuestionnaireStatus(
    @Id
    val id: UUID,

    @Enumerated(EnumType.STRING)
    val status: QuestionnaireStatus,

    val createdAt: Timestamp = Timestamp.from(Instant.now()),

    @ManyToOne(fetch = FetchType.LAZY)
    val questionnaire: DbQuestionnaire,

    val createdByUsername: String
)

interface QuestionnaireStatusDbInteractor : CrudRepository<DbQuestionnaireStatus, UUID>

@Entity
@Table(name = "questionnaire_submission_dues", schema = "questionnaire")
data class DbSubmissionDue(
    @Id
    val id: UUID,

    val weeks: Int,

    @OneToOne(fetch = FetchType.LAZY)
    val questionnaire: DbQuestionnaire,

    val createdByUsername: String
)

interface SubmissionDueDbInteractor : CrudRepository<DbSubmissionDue, UUID>

@Entity
@Table(name = "sections", schema = "questionnaire")
data class DbSection(
    @Id
    val id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    val questionnaire: DbQuestionnaire,

    val title: String,

    @OrderBy("created_at DESC")
    @OneToMany(mappedBy = "section", fetch = FetchType.LAZY)
    val statuses: List<DbSectionStatus> = emptyList(),

    @OrderBy("display_order ASC")
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "section")
    val sectionQuestions: List<DbSectionQuestion> = emptyList(),

    val displayOrder: Int,

    @OrderBy("created_at DESC")
    @OneToMany(mappedBy = "section")
    val assignees: List<DbAssignee> = emptyList(),

    val createdByUsername: String
)

interface SectionDbInteractor : CrudRepository<DbSection, UUID>

@Entity
@Table(name = "section_statuses", schema = "questionnaire")
data class DbSectionStatus(
    @Id val id: UUID,

    @Enumerated(EnumType.STRING)
    val status: SectionStatus,

    val createdAt: Timestamp = Timestamp.from(Instant.now()),

    @ManyToOne(fetch = FetchType.LAZY)
    val section: DbSection,

    val createdByUsername: String
)

interface SectionStatusDbInteractor : CrudRepository<DbSectionStatus, UUID>

@Entity
@Table(name = "sections_questions", schema = "questionnaire")
data class DbSectionQuestion(
    @Id
    val id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    val section: DbSection,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    val question: DbQuestion,

    @OrderBy("created_at DESC")
    @OneToMany(mappedBy = "sectionQuestion")
    val answers: List<DbAnswer> = emptyList(),

    @OrderBy("created_at DESC")
    @OneToMany(mappedBy = "sectionQuestion")
    val reviewStatuses: List<DbReviewStatus> = emptyList(),

    val displayOrder: Int,

    @OneToMany(mappedBy = "sectionQuestion")
    val supportingDocuments: List<DbSupportingDocument>,

    @OrderBy("created_at DESC")
    @OneToMany(mappedBy = "sectionQuestion")
    val requestsForClarification: List<DbRequestForClarification> = emptyList(),

    val createdByUsername: String
) {
    fun latestAnswerOrNull(): String? = this.answers.map { it.givenAnswer }.firstOrNull()
    fun requestForClarificationMessages(): List<String> = this.requestsForClarification.map { it.message }
    fun firstReviewStatusOrNull(): ReviewStatus? = this.reviewStatuses.firstOrNull()?.status
}

interface SectionQuestionDbInteractor : CrudRepository<DbSectionQuestion, UUID>

@Entity
@Table(name = "requests_for_clarification", schema = "questionnaire")
data class DbRequestForClarification(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    val sectionQuestion: DbSectionQuestion,

    val message: String,

    val createdByUsername: String
)

interface RequestsForClarificationDbInteractor : CrudRepository<DbRequestForClarification, UUID>

@Entity
@Table(name = "answers", schema = "questionnaire")
data class DbAnswer(
    @Id
    val id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    val sectionQuestion: DbSectionQuestion,

    val givenAnswer: String,

    val createdAt: Timestamp = Timestamp.from(Instant.now()),

    val createdByUsername: String
)

interface AnswerDbInteractor : CrudRepository<DbAnswer, UUID>

@Entity
@Table(name = "section_assignees", schema = "questionnaire")
data class DbAssignee(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    val section: DbSection,

    val username: String,

    val createdAt: Timestamp = Timestamp.from(Instant.now()),

    val createdByUsername: String
)

interface AssigneeDbInteractor : CrudRepository<DbAssignee, UUID>

@Entity
@Table(name = "question_review_statuses", schema = "questionnaire")
data class DbReviewStatus(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    val sectionQuestion: DbSectionQuestion,

    @Enumerated(EnumType.STRING)
    val status: ReviewStatus,

    val createdAt: Timestamp = Timestamp.from(Instant.now()),

    val createdByUsername: String

)

interface QuestionReviewStatusDbInteractor : CrudRepository<DbReviewStatus, UUID>
