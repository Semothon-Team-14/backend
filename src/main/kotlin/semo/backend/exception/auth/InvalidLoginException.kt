package semo.backend.exception.auth

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class InvalidLoginException : BaseCustomException(
    status = HttpStatus.UNAUTHORIZED,
    reasonTemplate = "Username or password is invalid",
)
