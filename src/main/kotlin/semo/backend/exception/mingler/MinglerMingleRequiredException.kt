package semo.backend.exception.mingler

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class MinglerMingleRequiredException : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "mingleId must not be null",
)
