package semo.backend.exception.request

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class InvalidRequestBodyException : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "Request body is invalid",
)
