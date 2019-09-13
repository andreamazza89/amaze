package com.mrdigitalpartners.oprah.web.dataTransferObjects

import java.util.UUID

data class QuestionUpdateRequest(val id: UUID, val newAnswer: String)