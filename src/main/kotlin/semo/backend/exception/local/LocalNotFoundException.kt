package semo.backend.exception.local

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class LocalNotFoundException(
    localId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Local not found for id={localId}",
    reasonVariables = mapOf("localId" to localId),
)
