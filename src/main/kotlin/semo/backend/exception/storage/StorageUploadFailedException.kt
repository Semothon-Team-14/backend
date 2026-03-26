package semo.backend.exception.storage

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class StorageUploadFailedException(
    objectKey: String,
) : BaseCustomException(
    status = HttpStatus.INTERNAL_SERVER_ERROR,
    reasonTemplate = "Failed to upload object to storage: {objectKey}",
    reasonVariables = mapOf("objectKey" to objectKey),
)
