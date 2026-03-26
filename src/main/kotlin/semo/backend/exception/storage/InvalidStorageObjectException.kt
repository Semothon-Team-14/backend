package semo.backend.exception.storage

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class InvalidStorageObjectException(
) : BaseCustomException(
    status = HttpStatus.BAD_REQUEST,
    reasonTemplate = "Uploaded file must not be empty",
)
