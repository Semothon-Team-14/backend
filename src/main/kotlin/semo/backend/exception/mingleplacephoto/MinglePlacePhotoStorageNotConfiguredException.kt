package semo.backend.exception.mingleplacephoto

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class MinglePlacePhotoStorageNotConfiguredException : BaseCustomException(
    status = HttpStatus.INTERNAL_SERVER_ERROR,
    reasonTemplate = "Mingle place photo storage is not configured. Set AWS credentials and places bucket settings.",
)
