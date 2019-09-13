package com.mrdigitalpartners.oprah.persistence

import com.mrdigitalpartners.oprah.core.TemplateQuestion
import com.mrdigitalpartners.oprah.core.User
import com.mrdigitalpartners.oprah.utils.pipe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Repository
class QuestionsRepository(@Autowired val questionDbInteractor: QuestionDbInteractor) {
    fun save(templateQuestion: TemplateQuestion, createdBy: User): TemplateQuestion =
        templateQuestion
            .pipe { dbTemplateQuestion(it, createdBy) }
            .pipe { questionDbInteractor.save(it) }
            .pipe { templateQuestion(it) }
}

fun dbTemplateQuestion(templateQuestion: TemplateQuestion, createdBy: User) =
    DbQuestion(
        templateQuestion.id,
        templateQuestion.label,
        templateQuestion.requiresSupportingDocuments,
        createdBy.username
    )

fun templateQuestion(question: DbQuestion) =
    TemplateQuestion(
        question.id,
        question.label,
        question.requiresSupportingDocuments
    )

@Entity
@Table(name = "questions", schema = "questionnaire")
data class DbQuestion(
    @Id
    val id: UUID,

    val label: String,

    val requiresSupportingDocuments: Boolean,

    val createdByUsername: String
)

interface QuestionDbInteractor : CrudRepository<DbQuestion, UUID>
