package semo.backend.exception.mingler

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class MinglerDuplicateException(
    userId: Long,
    mingleId: Long,
) : BaseCustomException(
    status = HttpStatus.CONFLICT,
    reasonTemplate = "Mingler already exists for userId={userId} and mingleId={mingleId}",
    reasonVariables = mapOf("userId" to userId, "mingleId" to mingleId),
)
