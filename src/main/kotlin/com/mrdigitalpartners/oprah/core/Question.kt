package com.mrdigitalpartners.oprah.core

import java.util.UUID

data class Question(
    val templateQuestion: TemplateQuestion,
    val answer: String? = null,
    val reviewDetails: ReviewDetails? = null,
    val id: UUID,
    val supportingDocuments: List<SupportingDocument>
) {
    fun approveAnswer(): Question = this.withReviewDetails(this.reviewDetails.approved())
    fun requestClarification(message: String): Question = this.withReviewDetails(this.reviewDetails.clarify(message))
    fun withAnswer(answer: String?): Question = this.copy(answer = answer)
    fun withReviewDetails(reviewDetails: ReviewDetails): Question = this.copy(reviewDetails = reviewDetails)
    fun isApproved(): Boolean = this.reviewDetails?.status == ReviewStatus.APPROVED
}

fun List<Question>.answerQuestion(update: QuestionUpdate<String>): List<Question> =
    this.updateQuestion(update.questionId) { it.withAnswer(update.newValue) }

fun List<Question>.requestClarification(update: QuestionUpdate<String>): List<Question> =
    this.updateQuestion(update.questionId) { it.requestClarification(update.newValue) }

fun List<Question>.approveAll(): List<Question> =
    this.map { it.approveAnswer() }

private fun List<Question>.updateQuestion(id: UUID, updater: (Question) -> Question): List<Question> =
    this.map { question ->
        if (question.id == id) {
            updater(question)
        } else {
            question
        }
    }

data class ReviewDetails(
    val status: ReviewStatus,
    val requestsForClarification: List<String>
)

private fun ReviewDetails?.approved(): ReviewDetails =
    this?.copy(status = ReviewStatus.APPROVED) ?: ReviewDetails(ReviewStatus.APPROVED, emptyList())

private fun ReviewDetails?.clarify(message: String): ReviewDetails =
    this?.copy(
        status = ReviewStatus.CLARIFICATION_REQUESTED,
        requestsForClarification = listOf(message) + this.requestsForClarification
    ) ?: ReviewDetails(ReviewStatus.CLARIFICATION_REQUESTED, listOf(message))

enum class ReviewStatus(string: String) {
    APPROVED("APPROVED"),
    CLARIFICATION_REQUESTED("CLARIFICATION_REQUESTED")
}
