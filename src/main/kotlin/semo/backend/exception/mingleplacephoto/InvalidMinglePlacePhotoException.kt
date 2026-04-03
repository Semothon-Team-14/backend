package semo.backend.exception.mingleplacephoto

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class InvalidMinglePlacePhotoException(
    reason: String,
) : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = reason,
)
