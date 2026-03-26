package semo.backend.storage

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import semo.backend.exception.storage.InvalidStorageObjectException
import semo.backend.exception.storage.StorageDeleteFailedException
import semo.backend.exception.storage.StorageUploadFailedException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

@Service
class S3StorageService(
    private val s3Client: S3Client,
    private val s3Properties: S3Properties,
) {
    fun upload(
        directory: String,
        file: MultipartFile,
    ): StorageObject {
        if (file.isEmpty) {
            throw InvalidStorageObjectException()
        }

        val objectKey = buildObjectKey(directory, file.originalFilename)
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(s3Properties.bucket)
            .key(objectKey)
            .contentType(file.contentType)
            .build()

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.bytes))
        } catch (_: Exception) {
            throw StorageUploadFailedException(objectKey)
        }

        return StorageObject(
            key = objectKey,
            url = buildUrl(objectKey),
        )
    }

    fun delete(objectKey: String) {
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(s3Properties.bucket)
            .key(objectKey)
            .build()

        try {
            s3Client.deleteObject(deleteObjectRequest)
        } catch (_: Exception) {
            throw StorageDeleteFailedException(objectKey)
        }
    }

    private fun buildObjectKey(
        directory: String,
        originalFilename: String?,
    ): String {
        val sanitizedFilename = originalFilename
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.substringAfterLast('/')
            ?.substringAfterLast('\\')
            ?: "image"
        return "${directory.trimEnd('/')}/${UUID.randomUUID()}-$sanitizedFilename"
    }

    private fun buildUrl(objectKey: String): String {
        val encodedObjectKey = objectKey.split('/').joinToString("/") {
            URLEncoder.encode(it, StandardCharsets.UTF_8).replace("+", "%20")
        }

        return s3Properties.endpoint
            ?.takeIf { it.isNotBlank() }
            ?.let { "${it.trimEnd('/')}/${s3Properties.bucket}/$encodedObjectKey" }
            ?: "https://${s3Properties.bucket}.s3.${s3Properties.region}.amazonaws.com/$encodedObjectKey"
    }
}
