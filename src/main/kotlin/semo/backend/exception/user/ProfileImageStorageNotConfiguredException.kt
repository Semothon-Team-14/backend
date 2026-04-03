package semo.backend.exception.user

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class ProfileImageStorageNotConfiguredException : BaseCustomException(
    status = HttpStatus.SERVICE_UNAVAILABLE,
    reasonTemplate = "Profile image storage is not configured",
)
