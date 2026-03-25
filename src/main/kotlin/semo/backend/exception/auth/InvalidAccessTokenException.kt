package semo.backend.exception.auth

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class InvalidAccessTokenException : BaseCustomException(
    status = HttpStatus.UNAUTHORIZED,
    reasonTemplate = "Access token is invalid",
)
