package com.mrdigitalpartners.oprah.persistence

import com.mrdigitalpartners.oprah.core.SupportingDocument
import com.mrdigitalpartners.oprah.core.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.transaction.Transactional

@Repository
class SupportingDocumentsRepository(
    @Autowired val supportingDocumentsFileDbInteractor: SupportingDocumentsFileDbInteractor,
    @Autowired val supportingDocumentsDbInteractor: SupportingDocumentsDbInteractor,
    @Autowired val sectionQuestionsDbInteractor: SectionQuestionDbInteractor
) {

    @Transactional
    fun save(data: ByteArray, sectionQuestionId: UUID, supportingDocument: SupportingDocument, createdBy: User): UUID {
        val dbSupportingDocumentFile = saveSupportingDocumentFile(data, supportingDocument.fileReference, createdBy)
        saveSupportingDocument(supportingDocument, dbSupportingDocumentFile, sectionQuestionId, createdBy)
        return supportingDocument.fileReference
    }

    fun findFileById(supportingDocumentFileId: UUID): Pair<String, ByteArray> {
        val data = supportingDocumentsFileDbInteractor.findById(supportingDocumentFileId).get().data
        val contentType = supportingDocumentsDbInteractor.findByFileId(supportingDocumentFileId).fileType
        return Pair(contentType, data)
    }

    private fun saveSupportingDocumentFile(data: ByteArray, id: UUID, createdBy: User): DbSupportingDocumentFile {
        return supportingDocumentsFileDbInteractor.save(DbSupportingDocumentFile(id, data, createdBy.username))
    }

    private fun saveSupportingDocument(
        supportingDocument: SupportingDocument,
        dbFile: DbSupportingDocumentFile,
        sectionQuestionId: UUID,
        createdBy: User
    ) {
        val sectionQuestion = sectionQuestionsDbInteractor.findById(sectionQuestionId).get()
        val dbSupportingDocument = DbSupportingDocument(
            supportingDocument.id,
            dbFile,
            sectionQuestion,
            supportingDocument.providedAsRequired,
            supportingDocument.fileName,
            supportingDocument.fileType,
            createdBy.username
        )
        supportingDocumentsDbInteractor.save(dbSupportingDocument)
    }
}

interface SupportingDocumentsDbInteractor : CrudRepository<DbSupportingDocument, UUID> {
    fun findByFileId(fileId: UUID): DbSupportingDocument
}

@Entity
@Table(name = "documents", schema = "supporting_documents")
data class DbSupportingDocument(
    @Id
    val id: UUID,

    @OneToOne
    val file: DbSupportingDocumentFile,

    @ManyToOne(fetch = FetchType.LAZY)
    val sectionQuestion: DbSectionQuestion,

    val providedAsRequired: Boolean,

    val fileName: String,

    val fileType: String,

    val createdByUsername: String
)

interface SupportingDocumentsFileDbInteractor : CrudRepository<DbSupportingDocumentFile, UUID>

@Entity
@Table(name = "files", schema = "supporting_documents")
class DbSupportingDocumentFile(
    @Id
    val id: UUID,

    val data: ByteArray,

    val createdByUsername: String
)
