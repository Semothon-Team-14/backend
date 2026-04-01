package semo.backend.exception.mingle

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class MingleTitleRequiredException : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "title must not be null",
)
