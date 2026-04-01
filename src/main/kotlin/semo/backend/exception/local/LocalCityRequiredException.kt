package semo.backend.exception.local

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class LocalCityRequiredException : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "cityId must not be null",
)
