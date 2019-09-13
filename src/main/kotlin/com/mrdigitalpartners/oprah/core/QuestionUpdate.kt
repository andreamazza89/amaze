package com.mrdigitalpartners.oprah.core

import java.util.UUID

data class QuestionUpdate<T>(val sectionTitle: String, val questionId: UUID, val newValue: T)