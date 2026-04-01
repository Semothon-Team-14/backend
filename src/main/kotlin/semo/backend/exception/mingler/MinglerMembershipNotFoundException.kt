package semo.backend.exception.mingler

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class MinglerMembershipNotFoundException(
    userId: Long,
    mingleId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Mingler not found for userId={userId} and mingleId={mingleId}",
    reasonVariables = mapOf("userId" to userId, "mingleId" to mingleId),
)
