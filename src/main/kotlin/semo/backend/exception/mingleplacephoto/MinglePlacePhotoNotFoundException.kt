package semo.backend.exception.mingleplacephoto

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class MinglePlacePhotoNotFoundException(
    photoId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Mingle place photo not found for id={photoId}",
    reasonVariables = mapOf("photoId" to photoId),
)
