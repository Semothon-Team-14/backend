package semo.backend.exception.keyword

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class InvalidKeywordPriorityException(
    priority: Int,
) : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "Keyword priority must be between 1 and 10: {priority}",
    reasonVariables = mapOf("priority" to priority),
)
