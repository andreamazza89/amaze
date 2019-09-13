package com.mrdigitalpartners.oprah.persistence

import com.mrdigitalpartners.oprah.core.Question
import com.mrdigitalpartners.oprah.core.Questionnaire
import com.mrdigitalpartners.oprah.core.QuestionnaireStatus
import com.mrdigitalpartners.oprah.core.ReviewDetails
import com.mrdigitalpartners.oprah.core.ReviewStatus
import com.mrdigitalpartners.oprah.core.Section
import com.mrdigitalpartners.oprah.core.SectionStatus
import com.mrdigitalpartners.oprah.core.SupportingDocument
import com.mrdigitalpartners.oprah.core.User
import com.mrdigitalpartners.oprah.utils.pipe
import com.mrdigitalpartners.oprah.utils.toUtc
import com.mrdigitalpartners.oprah.utils.utcTimeNow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
class QuestionnairesRepository(
    @Autowired val questionnaireDbInteractor: QuestionnaireDbInteractor,
    @Autowired val questionnaireStatusDbInteractor: QuestionnaireStatusDbInteractor,
    @Autowired val submissionDueDbInteractor: SubmissionDueDbInteractor,
    @Autowired val sectionDbInteractor: SectionDbInteractor,
    @Autowired val sectionStatusDbInteractor: SectionStatusDbInteractor,
    @Autowired val sectionQuestionsDbInteractor: SectionQuestionDbInteractor,
    @Autowired val requestsForClarificationDbInteractor: RequestsForClarificationDbInteractor,
    @Autowired val answerDbInteractor: AnswerDbInteractor,
    @Autowired val assigneeDbInteractor: AssigneeDbInteractor,
    @Autowired val questionReviewStatusDbInteractor: QuestionReviewStatusDbInteractor
) {
    @Transactional
    fun save(partnerId: String, questionnaire: Questionnaire, createdBy: User) {
        val dbQuestionnaire = DbQuestionnaire(
            id = questionnaire.id,
            partnerId = partnerId,
            name = questionnaire.name,
            createdByUsername = createdBy.username
        )
        questionnaireDbInteractor.save(dbQuestionnaire)
        questionnaireStatusDbInteractor.save(dbQuestionnaireStatus(questionnaire.status, dbQuestionnaire, createdBy))
        saveSections(questionnaire.sections, dbQuestionnaire, createdBy)
    }

    private fun saveSections(sections: List<Section>, dbQuestionnaire: DbQuestionnaire, createdBy: User) {
        sections.forEachIndexed { sectionIndex, section ->
            val dbSection = saveSection(dbQuestionnaire, section, sectionIndex, createdBy)
            sectionStatusDbInteractor.save(dbSectionStatus(Pair(dbSection, section.status), createdBy))
            saveQuestions(section.questions, dbSection, createdBy)
        }
    }

    private fun saveSection(dbQuestionnaire: DbQuestionnaire, section: Section, sectionIndex: Int, createdBy: User) =
        sectionDbInteractor.save(
            dbSection(dbQuestionnaire, section, sectionIndex, createdBy)
        )

    private fun saveQuestions(questions: List<Question>, dbSection: DbSection, createdBy: User) =
        questions.mapIndexed { index, question -> dbSectionQuestion(question, dbSection, index, createdBy) }
            .pipe { sectionQuestionsDbInteractor.saveAll(it) }

    @Transactional
    fun update(changes: QuestionnaireChanges, createdBy: User) {
        val dbQuestionnaire = questionnaireDbInteractor.findById(changes.id).get()

        changes.invokeIfQuestionnaireStatusChanged {
            questionnaireStatusDbInteractor.save(dbQuestionnaireStatus(it, dbQuestionnaire, createdBy))
        }

        changes.invokeIfSubmissionDueInWeeksChanges {
            submissionDueDbInteractor.save(dbSubmissionDue(it, dbQuestionnaire, createdBy))
        }

        changes.invokeIfSectionPropertyChanged(SectionChanges::status) { sectionsChanges ->
            sectionsChanges
                .mapSectionTitleToDbSection(dbQuestionnaire.sections)
                .map { dbSectionStatus(it, createdBy) }
                .pipe { sectionStatusDbInteractor.saveAll(it) }
        }

        changes.invokeIfSectionPropertyChanged(SectionChanges::assignedTo) { sectionsChanges ->
            sectionsChanges
                .mapSectionTitleToDbSection(dbQuestionnaire.sections)
                .map { dbAssignee(it, createdBy) }
                .pipe { assigneeDbInteractor.saveAll(it) }
        }

        changes.invokeIfQuestionPropertyChanged(QuestionChanges::answer) { questionsChanges ->
            questionsChanges
                .mapQuestionIdToDbSectionQuestion(dbQuestionnaire.sections.flatMap { it.sectionQuestions })
                .map { dbAnswer(it, createdBy) }
                .pipe { answerDbInteractor.saveAll(it) }
        }

        changes.invokeIfQuestionPropertyChanged(QuestionChanges::clarificationMessage) { questionsChanges ->
            questionsChanges
                .mapQuestionIdToDbSectionQuestion(dbQuestionnaire.sections.flatMap { it.sectionQuestions })
                .map { dbRequestForClarification(it, createdBy) }
                .pipe { requestsForClarificationDbInteractor.saveAll(it) }
        }

        changes.invokeIfQuestionPropertyChanged(QuestionChanges::reviewStatus) { questionsChanges ->
            questionsChanges
                .mapQuestionIdToDbSectionQuestion(dbQuestionnaire.sections.flatMap { it.sectionQuestions })
                .map { dbReviewStatus(it, createdBy) }
                .pipe { questionReviewStatusDbInteractor.saveAll(it) }
        }
    }

    private fun <T> Map<String, T>.mapSectionTitleToDbSection(
        dbSections: List<DbSection>
    ): List<Pair<DbSection, T>> = this.map { (title, change) -> Pair(findDbSectionByTitle(title, dbSections), change) }

    private fun <T> Map<UUID, T>.mapQuestionIdToDbSectionQuestion(
        dbQuestions: List<DbSectionQuestion>
    ): List<Pair<DbSectionQuestion, T>> = this.map { (id, change) -> Pair(findDbQuestionById(id, dbQuestions), change) }

    private fun findDbSectionByTitle(title: String, dbSections: List<DbSection>): DbSection =
        dbSections.find { it.title == title }!!

    private fun findDbQuestionById(id: UUID, dbQuestions: List<DbSectionQuestion>): DbSectionQuestion =
        dbQuestions.find { it.id == id }!!

    @Transactional
    fun find(id: UUID): Questionnaire {
        return toQuestionnaire(questionnaireDbInteractor.findById(id).get())
    }

    @Transactional
    fun findAll(partnerId: String): Iterable<Questionnaire> {
        return questionnaireDbInteractor.findAllByPartnerId(partnerId).map(::toQuestionnaire)
    }

    fun deleteAll() {
        questionnaireDbInteractor.deleteAll()
        sectionDbInteractor.deleteAll()
    }
}

// to database entity
private fun dbQuestionnaireStatus(status: QuestionnaireStatus, dbQuestionnaire: DbQuestionnaire, createdBy: User) =
    DbQuestionnaireStatus(
        id = UUID.randomUUID(),
        status = status,
        questionnaire = dbQuestionnaire,
        createdByUsername = createdBy.username
    )

private fun dbSubmissionDue(submissionDue: Int, dbQuestionnaire: DbQuestionnaire, createdBy: User) =
    DbSubmissionDue(
        id = UUID.randomUUID(),
        weeks = submissionDue,
        questionnaire = dbQuestionnaire,
        createdByUsername = createdBy.username
    )

private fun dbSection(dbQuestionnaire: DbQuestionnaire, section: Section, sectionIndex: Int, createdBy: User) =
    DbSection(
        id = UUID.randomUUID(),
        questionnaire = dbQuestionnaire,
        title = section.title,
        displayOrder = sectionIndex,
        createdByUsername = createdBy.username
    )

private fun dbSectionStatus(dbSectionToNewStatus: Pair<DbSection, SectionStatus>, createdBy: User) =
    DbSectionStatus(
        id = UUID.randomUUID(),
        status = dbSectionToNewStatus.second,
        section = dbSectionToNewStatus.first,
        createdByUsername = createdBy.username
    )

private fun dbAssignee(dbSectionToNewAssignee: Pair<DbSection, String>, createdBy: User) =
    DbAssignee(
        id = UUID.randomUUID(),
        section = dbSectionToNewAssignee.first,
        username = dbSectionToNewAssignee.second,
        createdByUsername = createdBy.username
    )

private fun dbSectionQuestion(question: Question, dbSection: DbSection, index: Int, createdBy: User) =
    DbSectionQuestion(
        id = question.id,
        section = dbSection,
        question = dbTemplateQuestion(question.templateQuestion, createdBy),
        displayOrder = index,
        supportingDocuments = emptyList(),
        requestsForClarification = emptyList(),
        createdByUsername = createdBy.username
    )

private fun dbAnswer(dbQuestionToNewAnswer: Pair<DbSectionQuestion, String>, createdBy: User) =
    DbAnswer(
        id = UUID.randomUUID(),
        sectionQuestion = dbQuestionToNewAnswer.first,
        givenAnswer = dbQuestionToNewAnswer.second,
        createdByUsername = createdBy.username
    )

private fun dbRequestForClarification(
    dbQuestionToNewRequestsForClarification: Pair<DbSectionQuestion, String>,
    createdBy: User
) =
    DbRequestForClarification(
        sectionQuestion = dbQuestionToNewRequestsForClarification.first,
        message = dbQuestionToNewRequestsForClarification.second,
        createdByUsername = createdBy.username
    )

private fun dbReviewStatus(dbQuestionToNewReviewStatus: Pair<DbSectionQuestion, ReviewStatus>, createdBy: User) =
    DbReviewStatus(
        sectionQuestion = dbQuestionToNewReviewStatus.first,
        status = dbQuestionToNewReviewStatus.second,
        createdByUsername = createdBy.username
    )

// from database entity
private fun toQuestionnaire(dbQuestionnaire: DbQuestionnaire) =
    Questionnaire(
        id = dbQuestionnaire.id,
        sections = dbQuestionnaire.sections.map(::toSection),
        name = dbQuestionnaire.name,
        status = dbQuestionnaire.statuses.first().status,
        submissionDueInWeeks = dbQuestionnaire.submissionDueInWeeks?.weeks,
        dueDateStatus = toQuestionnaireDueDateStatus(
            submissionDueInWeeks = dbQuestionnaire.submissionDueInWeeks?.weeks,
            utcTimeNow = utcTimeNow(),
            statusTimes = toSectionStatusTimes(dbQuestionnaire)
        ),
        sentToPartnerAt = dbQuestionnaire.submittedToPartnerAt()
    )

fun toSectionStatusTimes(dbQuestionnaire: DbQuestionnaire): SectionStatusTimes {
    return dbQuestionnaire.sections.map { dbSection ->
        dbSection.statuses.map { dbStatus ->
            SectionStatusTime(
                dbStatus.status,
                dbStatus.createdAt.toUtc()
            )
        }
    }
}

private fun toSection(dbSection: DbSection) =
    Section(
        assignedTo = dbSection.assignees.firstOrNull()?.username,
        title = dbSection.title,
        questions = dbSection.sectionQuestions.map(::toQuestion),
        status = dbSection.statuses.first().status
    )

private fun toQuestion(dbSectionQuestion: DbSectionQuestion) =
    Question(
        templateQuestion = templateQuestion(dbSectionQuestion.question),
        answer = dbSectionQuestion.latestAnswerOrNull(),
        id = dbSectionQuestion.id,
        reviewDetails = toReviewDetails(dbSectionQuestion),
        supportingDocuments = dbSectionQuestion.supportingDocuments.map(::toSupportingDocument)
    )

private fun toReviewDetails(dbSectionQuestion: DbSectionQuestion): ReviewDetails? =
    if (dbSectionQuestion.firstReviewStatusOrNull() == null) {
        null
    } else {
        ReviewDetails(
            status = dbSectionQuestion.firstReviewStatusOrNull()!!,
            requestsForClarification = dbSectionQuestion.requestForClarificationMessages()
        )
    }

private fun toSupportingDocument(dbSupportingDocument: DbSupportingDocument) =
    SupportingDocument(
        id = dbSupportingDocument.id,
        fileReference = dbSupportingDocument.file.id,
        providedAsRequired = dbSupportingDocument.providedAsRequired,
        fileName = dbSupportingDocument.fileName,
        fileType = dbSupportingDocument.fileType
    )
