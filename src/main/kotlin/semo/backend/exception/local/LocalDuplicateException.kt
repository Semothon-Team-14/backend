package semo.backend.exception.local

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class LocalDuplicateException(
    userId: Long,
    cityId: Long,
) : BaseCustomException(
    status = HttpStatus.CONFLICT,
    reasonTemplate = "Local already exists for userId={userId} and cityId={cityId}",
    reasonVariables = mapOf("userId" to userId, "cityId" to cityId),
)
