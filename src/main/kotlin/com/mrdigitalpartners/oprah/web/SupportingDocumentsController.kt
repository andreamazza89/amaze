package com.mrdigitalpartners.oprah.web

import com.mrdigitalpartners.oprah.core.SupportingDocument
import com.mrdigitalpartners.oprah.core.User
import com.mrdigitalpartners.oprah.persistence.SupportingDocumentsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@CrossOrigin
@RequestMapping("/supporting-documents")
class SupportingDocumentsController(@Autowired val supportingDocumentsRepository: SupportingDocumentsRepository) {

    @PostMapping
    fun uploadSupportingDocument(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("providedAsRequired") providedAsRequired: Boolean,
        @RequestParam("sectionQuestionId") sectionQuestionId: String,
        @RequestHeader("username") username: String
    ): UUID {
        val supportingDocument = SupportingDocument(
            id = UUID.randomUUID(),
            fileReference = UUID.randomUUID(),
            providedAsRequired = providedAsRequired,
            fileName = file.originalFilename!!, // this should never be null (our frontend enforces providing a file)
            fileType = file.contentType!! // this should never be null (our frontend enforces providing a mimetype)
        )
        return supportingDocumentsRepository.save(file.bytes, UUID.fromString(sectionQuestionId), supportingDocument, User(username))
    }

    @GetMapping(path = ["{supportingDocumentFileId}"], produces = [MediaType.ALL_VALUE])
    fun getSupportingDocumentFile(@PathVariable supportingDocumentFileId: UUID): ResponseEntity<ByteArray> {
        val (contentType, data) = supportingDocumentsRepository.findFileById(supportingDocumentFileId)
        return ResponseEntity
            .ok()
            .header("Content-Type", contentType)
            .body(data)
    }
}
