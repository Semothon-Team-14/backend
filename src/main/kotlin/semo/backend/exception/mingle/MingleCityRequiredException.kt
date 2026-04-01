package semo.backend.exception.mingle

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class MingleCityRequiredException : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "cityId must not be null",
)
