package semo.backend.exception.cafeimage

import org.springframework.http.HttpStatus
import semo.backend.exception.BaseCustomException

class CafeImageNotFoundException(
    cafeImageId: Long,
) : BaseCustomException(
    status = HttpStatus.NOT_FOUND,
    reasonTemplate = "Cafe image not found for id={cafeImageId}",
    reasonVariables = mapOf("cafeImageId" to cafeImageId),
)
