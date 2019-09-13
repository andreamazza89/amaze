package com.mrdigitalpartners.oprah.core

import java.util.UUID

data class SupportingDocument(
    val id: UUID,
    val fileReference: UUID,
    val providedAsRequired: Boolean,
    val fileName: String,
    val fileType: String
)
