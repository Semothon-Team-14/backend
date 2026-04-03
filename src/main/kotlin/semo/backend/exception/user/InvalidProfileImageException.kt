package semo.backend.exception.user

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class InvalidProfileImageException(
    reason: String,
) : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = reason,
)
