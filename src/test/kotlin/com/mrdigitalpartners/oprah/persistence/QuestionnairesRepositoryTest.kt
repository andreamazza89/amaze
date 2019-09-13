package com.mrdigitalpartners.oprah.persistence

import com.mrdigitalpartners.oprah.core.CoreHelper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuestionnairesRepositoryTest(
    @Autowired val repository: QuestionnairesRepository,
    @Autowired val databaseHelper: DatabaseHelper
) {

    val coreHelper = CoreHelper()

    @AfterEach
    fun teardown() {
        databaseHelper.teardown()
    }

    @Test
    fun `saves a questionnaire for a partner`() {
        val id = UUID.randomUUID()
        val questionOne = databaseHelper.createQuestion(label = "would you consider bread rolls?")
        val questionTwo = databaseHelper.createQuestion(label = "have you ever considered reinsuring?")
        val questionThree = databaseHelper.createQuestion(label = "what is your name?")
        val questionnaire = coreHelper.newQuestionnaire(
                id = id,
                sections = *arrayOf(
                        coreHelper.notStartedSection(questionOne, title = "section 1"),
                        coreHelper.notStartedSection(questionTwo, questionThree, title = "section 2")
                )
        )
        repository.save("BBM", questionnaire, coreHelper.user())

        val questionnaireFound = repository.find(id)
        assertEquals(questionnaire, questionnaireFound)
    }

    @Test
    fun `finds a questionnaire by partner`() {
        val partnerOneId = "BBM"
        val partnerTwoId = "OTHER_PARTNER"

        databaseHelper.saveQuestionnaire(UUID.randomUUID(), partnerOneId)

        val questionnairesForPartnerOne = repository.findAll(partnerOneId)
        val questionnairesForPartnerTwo = repository.findAll(partnerTwoId)
        assertEquals(1, questionnairesForPartnerOne.count())
        assertEquals(0, questionnairesForPartnerTwo.count())
    }

    @Test
    fun `orders the sections and questions in the given order`() {
        val questionnaireId = UUID.randomUUID()
        val firstSectionTitle = "Section 0"
        val secondSectionTitle = "Section 1"
        val questionA = databaseHelper.createQuestion(label = "do you accept flour payments?")
        val questionB = databaseHelper.createQuestion(label = "have you ever considered reinsuring?")
        val questionC = databaseHelper.createQuestion(label = "would you like a free car?")

        val questionnaire = coreHelper.newQuestionnaire(
                id = questionnaireId,
                sections = *arrayOf(
                        coreHelper.notStartedSection(questionA, questionB, title = firstSectionTitle),
                        coreHelper.notStartedSection(questionC, title = secondSectionTitle)
                )
        )

        repository.save("BBM", questionnaire, coreHelper.user())

        val sections = repository.find(questionnaireId).sections
        assertEquals(firstSectionTitle, sections[0].title)
        assertEquals(secondSectionTitle, sections[1].title)
        assertEquals(listOf(questionA, questionB), sections[0].questions)
    }
}
