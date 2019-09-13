package com.mrdigitalpartners.oprah.persistence

import com.mrdigitalpartners.oprah.core.TemplateQuestionnaire
import com.mrdigitalpartners.oprah.core.TemplateSection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.io.Serializable
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OrderBy
import javax.persistence.Table

@Repository
class QuestionnaireTemplatesRepository(
    @Autowired val dbQuestionnaireTemplateInteractor: DbQuestionnaireTemplateInteractor
) {
    fun findAll(): List<TemplateQuestionnaire> {
        return dbQuestionnaireTemplateInteractor.findAll().map(::toTemplateQuestionnaire)
    }
}

// from database entity
private fun toTemplateQuestionnaire(dbTemplateQuestionnaire: DbTemplateQuestionnaire) =
    TemplateQuestionnaire(
        dbTemplateQuestionnaire.name,
        dbTemplateQuestionnaire.sections.map { toTemplateSection(it) }
    )

private fun toTemplateSection(dbTemplateSection: DbTemplateSection) =
    TemplateSection(
        dbTemplateSection.title,
        dbTemplateSection.sectionQuestions.map { templateQuestion(it.question) })

@Entity
@Table(name = "templates", schema = "questionnaire")
data class DbTemplateQuestionnaire(
    @Id
    val id: UUID,

    val name: String,

    @OneToMany(mappedBy = "template")
    @OrderBy("display_order ASC")
    val sections: List<DbTemplateSection>
)

interface DbQuestionnaireTemplateInteractor : CrudRepository<DbTemplateQuestionnaire, UUID>

@Entity
@Table(name = "template_sections", schema = "questionnaire")
data class DbTemplateSection(
    @Id
    val id: UUID,

    val title: String,

    @OrderBy("display_order ASC")
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "section")
    val sectionQuestions: List<DbTemplateSectionQuestion>,

    @ManyToOne(fetch = FetchType.LAZY)
    val template: DbTemplateQuestionnaire,

    val displayOrder: Int
)

interface DbTemplateSectionsDbInteractor : CrudRepository<DbTemplateSection, UUID>

@Entity
@Table(name = "template_sections_questions", schema = "questionnaire")
@IdClass(DbTemplateSectionQuestionId::class)
data class DbTemplateSectionQuestion(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_section_id")
    val section: DbSection,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    val question: DbQuestion,

    val displayOrder: Int
) : Serializable

data class DbTemplateSectionQuestionId(
    val section: UUID = UUID.randomUUID(),
    val question: UUID = UUID.randomUUID()
) : Serializable

interface TemplateSectionQuestionDbInteractor : CrudRepository<DbTemplateSectionQuestion, DbTemplateSectionQuestionId>
