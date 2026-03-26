package semo.backend.exception.storage

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class StorageDeleteFailedException(
    objectKey: String,
) : BaseCustomException(
    status = HttpStatus.INTERNAL_SERVER_ERROR,
    reasonTemplate = "Failed to delete object from storage: {objectKey}",
    reasonVariables = mapOf("objectKey" to objectKey),
)
