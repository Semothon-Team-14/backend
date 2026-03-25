package semo.backend.exception.user

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class UserNotFoundException(
    userId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "User not found for id={userId}",
    reasonVariables = mapOf("userId" to userId),
)
