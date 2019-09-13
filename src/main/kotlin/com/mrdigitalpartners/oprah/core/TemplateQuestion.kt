package com.mrdigitalpartners.oprah.core

import java.util.*

data class TemplateQuestion(
    val id: UUID = UUID.randomUUID(),
    val label: String,
    val requiresSupportingDocuments: Boolean
)
