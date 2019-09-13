package com.mrdigitalpartners.oprah.web.dataTransferObjects

import org.springframework.web.multipart.MultipartFile
import java.util.UUID

data class CreateSupportingDocumentRequest(
    val sectionQuestionId: UUID,
    val data: MultipartFile
)
